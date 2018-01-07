package farijo.com.starcraft_bo_shower.player;

import android.support.annotation.DrawableRes;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import farijo.com.starcraft_bo_shower.R;

/**
 * Created by Teddy on 21/07/2017.
 */

public class SC2Action {

    private static Map<String, Integer> strToResource = null;
    private static void initialize() {
        strToResource = new HashMap<>();
        strToResource.put("ability_protoss_blink_color", R.drawable.ability_protoss_blink_color);
        strToResource.put("ability_protoss_charge_color", R.drawable.ability_protoss_charge_color);
        strToResource.put("building_protoss_assimilator", R.drawable.building_protoss_assimilator);
        strToResource.put("building_protoss_cyberneticscore", R.drawable.building_protoss_cyberneticscore);
        strToResource.put("building_protoss_darkshrine", R.drawable.building_protoss_darkshrine);
        strToResource.put("building_protoss_forge", R.drawable.building_protoss_forge);
        strToResource.put("building_protoss_gateway", R.drawable.building_protoss_gateway);
        strToResource.put("building_protoss_nexus", R.drawable.building_protoss_nexus);
        strToResource.put("building_protoss_pylon", R.drawable.building_protoss_pylon);
        strToResource.put("building_protoss_roboticsfacility", R.drawable.building_protoss_roboticsfacility);
        strToResource.put("building_protoss_roboticssupportbay", R.drawable.building_protoss_roboticssupportbay);
        strToResource.put("building_protoss_twilightcouncil", R.drawable.building_protoss_twilightcouncil);
        strToResource.put("building_protoss_warpgate", R.drawable.building_protoss_warpgate);
        strToResource.put("unit_protoss_adept", R.drawable.unit_protoss_adept);
        strToResource.put("unit_protoss_darktemplar", R.drawable.unit_protoss_darktemplar);
        strToResource.put("unit_protoss_disruptor", R.drawable.unit_protoss_disruptor);
        strToResource.put("unit_protoss_immortal", R.drawable.unit_protoss_immortal);
        strToResource.put("unit_protoss_mothershipcore", R.drawable.unit_protoss_mothershipcore);
        strToResource.put("unit_protoss_observer", R.drawable.unit_protoss_observer);
        strToResource.put("unit_protoss_sentry", R.drawable.unit_protoss_sentry);
        strToResource.put("unit_protoss_stalker", R.drawable.unit_protoss_stalker);
        strToResource.put("unit_protoss_warpprism", R.drawable.unit_protoss_warpprism);
        strToResource.put("unit_protoss_zealot", R.drawable.unit_protoss_zealot);
        strToResource.put("upgrade_protoss_groundarmorlevel1", R.drawable.upgrade_protoss_groundarmorlevel1);
        strToResource.put("upgrade_protoss_groundweaponslevel1", R.drawable.upgrade_protoss_groundweaponslevel1);
        strToResource.put("upgrade_protoss_resonatingglaives", R.drawable.upgrade_protoss_resonatingglaives);
    }

    @DrawableRes
    static int getDrawableId(String value) {
        if(strToResource == null) {
            initialize();
        }
        if(strToResource.containsKey(value)) {
            return strToResource.get(value);
        } else {
            return 0;
        }
    }

    public String population;
    public String strTiming;
    int onFinish = -1;
    long deltaTiming;
    int timing;
    public String name;
    public int ressourceIcon;
    public int count = 1;
    public String details;
    public View view;

    SC2Action() {

    }

    @Override
    public String toString() {
        return population+" "+timing+" "+name+" "+details;
    }
}
