package game.items;

import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.MerchantContext;
import game.actors.Farmer;
import game.actors.Merchant;
import game.crops.ParsnipItem;
import game.statistics.FarmerStatistics;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests buying a Sword: the buyer always pays, and half the time the
 * Merchant hands over nothing.
 */
class SwordTest {

    private static final int STARTING_COINS = 10000;

    private final FixedRandom random = new FixedRandom();
    private final Farmer buyer = new Farmer("Test");
    private final GameMap map = TestFarm.map("...");
    private final MerchantContext merchant = new MerchantContext(new Merchant(random), map.at(1, 0));

    @Test
    void isSoldUnderItsNameAndPrice() {
        Sword sword = new Sword(random);

        assertEquals("Sword", sword.getProductName());
        assertEquals(3000, sword.getPrice());
    }

    @Test
    void theBuyerPaysAndGetsANewSword() {
        random.setRollsSucceed(false);

        String report = new Sword(random).purchase(buyer, merchant);

        assertEquals(STARTING_COINS - 3000, buyer.getStatistic(FarmerStatistics.COINS));
        assertEquals(1, buyer.getInventory().getItems().size());
        assertEquals("Sword", buyer.getInventory().getItems().get(0).toString());
        assertTrue(report.contains("buys a Sword"), report);
    }

    @Test
    void sometimesTheMerchantKeepsTheCoinsAndHandsOverNothing() {
        random.setRollsSucceed(true);

        String report = new Sword(random).purchase(buyer, merchant);

        assertEquals(STARTING_COINS - 3000, buyer.getStatistic(FarmerStatistics.COINS));
        assertTrue(buyer.getInventory().getItems().isEmpty());
        assertTrue(report.contains("hands over nothing"), report);
    }

    @Test
    void buyingNeverTouchesTheRestOfTheInventory() {
        random.setRollsSucceed(true);
        buyer.getInventory().add(new ParsnipItem());

        new Sword(random).purchase(buyer, merchant);

        assertEquals(1, buyer.getInventory().getItems().size(), "the Parsnip is still there");
    }
}
