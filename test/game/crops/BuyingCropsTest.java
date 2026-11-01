package game.crops;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.MerchantContext;
import game.actors.Farmer;
import game.actors.Merchant;
import game.positions.area.AreaFactory;
import game.statistics.FarmerStatistics;
import game.testsupport.FixedRandom;
import game.testsupport.TestFarm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests buying a Parsnip or a Tomato crop: the crop sells itself, and the
 * Merchant plants the copy that was paid for.
 */
class BuyingCropsTest {

    private static final int STARTING_COINS = 10000;

    private final FixedRandom random = new FixedRandom();
    private final AreaFactory areaFactory = new AreaFactory(random);
    private final Farmer buyer = new Farmer("Test");
    private final Merchant merchant = new Merchant(random);

    @Test
    void parsnipIsSoldUnderItsNameAndPrice() {
        Parsnip parsnip = new Parsnip(random, areaFactory);

        assertEquals("Parsnip crop", parsnip.getProductName());
        assertEquals(250, parsnip.getPrice());
    }

    @Test
    void buyingAParsnipPlantsOneChargesAndDropsThreeParsnips() {
        GameMap map = TestFarm.map(",,,", ",.,", ",,,");

        String report = new Parsnip(random, areaFactory).purchase(buyer, at(map.at(1, 1)));

        assertEquals(1, TestFarm.countGround(map, Parsnip.class));
        assertEquals(3, TestFarm.countItems(map));
        assertEquals(STARTING_COINS - 250, buyer.getStatistic(FarmerStatistics.COINS));
        assertTrue(report.contains("buys a Parsnip crop"), report);
        assertTrue(report.contains("3 Parsnips drop at ("), "says where the bonus Parsnips landed: " + report);
    }

    @Test
    void nothingIsChargedWhenThereIsNowhereToPlant() {
        GameMap map = TestFarm.map("...", "...", "...");

        String report = new Parsnip(random, areaFactory).purchase(buyer, at(map.at(1, 1)));

        assertEquals(0, TestFarm.countGround(map, Parsnip.class));
        assertEquals(STARTING_COINS, buyer.getStatistic(FarmerStatistics.COINS));
        assertTrue(report.contains("no room"), report);
    }

    @Test
    void tomatoIsSoldUnderItsNameAndPrice() {
        Tomato tomato = new Tomato(random, areaFactory);

        assertEquals("Tomato crop", tomato.getProductName());
        assertEquals(500, tomato.getPrice());
    }

    @Test
    void aTomatoIsSometimesPlantedForFree() {
        random.setRollsSucceed(true);
        GameMap map = TestFarm.map(",,,", ",.,", ",,,");

        String report = new Tomato(random, areaFactory).purchase(buyer, at(map.at(1, 1)));

        assertEquals(1, TestFarm.countGround(map, Tomato.class));
        assertEquals(STARTING_COINS, buyer.getStatistic(FarmerStatistics.COINS));
        assertTrue(report.contains("for free"), report);
    }

    @Test
    void otherwiseATomatoIsCharged() {
        random.setRollsSucceed(false);
        GameMap map = TestFarm.map(",,,", ",.,", ",,,");

        new Tomato(random, areaFactory).purchase(buyer, at(map.at(1, 1)));

        assertEquals(1, TestFarm.countGround(map, Tomato.class));
        assertEquals(STARTING_COINS - 500, buyer.getStatistic(FarmerStatistics.COINS));
    }

    /**
     * @param merchantTile where the Merchant stands
     * @return the Merchant standing there
     */
    private MerchantContext at(Location merchantTile) {
        return new MerchantContext(merchant, merchantTile);
    }
}
