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
        strToResource.put("ability_protoss_psistorm_color", R.drawable.ability_protoss_psistorm_color);
        strToResource.put("ability_terran_cloak_color", R.drawable.ability_terran_cloak_color);
        strToResource.put("ability_terran_huntermissile_color", R.drawable.ability_terran_huntermissile_color);
        strToResource.put("ability_terran_nuclearstrike_color", R.drawable.ability_terran_nuclearstrike_color);
        strToResource.put("ability_terran_punishergrenade_color", R.drawable.ability_terran_punishergrenade_color);
        strToResource.put("ability_terran_stimpack_color", R.drawable.ability_terran_stimpack_color);
        strToResource.put("ability_terran_yamatogun_color", R.drawable.ability_terran_yamatogun_color);
        strToResource.put("ability_zerg_fungalgrowth_color", R.drawable.ability_zerg_fungalgrowth_color);
        strToResource.put("ability_zerg_neuralparasite_color", R.drawable.ability_zerg_neuralparasite_color);
        strToResource.put("armor_zerg_building", R.drawable.armor_zerg_building);
        strToResource.put("building_protoss_assimilator", R.drawable.building_protoss_assimilator);
        strToResource.put("building_protoss_cyberneticscore", R.drawable.building_protoss_cyberneticscore);
        strToResource.put("building_protoss_darkshrine", R.drawable.building_protoss_darkshrine);
        strToResource.put("building_protoss_energycrystal", R.drawable.building_protoss_energycrystal);
        strToResource.put("building_protoss_eyeofadun", R.drawable.building_protoss_eyeofadun);
        strToResource.put("building_protoss_fleetbeacon", R.drawable.building_protoss_fleetbeacon);
        strToResource.put("building_protoss_forge", R.drawable.building_protoss_forge);
        strToResource.put("building_protoss_gateway", R.drawable.building_protoss_gateway);
        strToResource.put("building_protoss_nexus", R.drawable.building_protoss_nexus);
        strToResource.put("building_protoss_obelisk", R.drawable.building_protoss_obelisk);
        strToResource.put("building_protoss_phasecannon", R.drawable.building_protoss_phasecannon);
        strToResource.put("building_protoss_photoncannon", R.drawable.building_protoss_photoncannon);
        strToResource.put("building_protoss_pylon", R.drawable.building_protoss_pylon);
        strToResource.put("building_protoss_roboticsfacility", R.drawable.building_protoss_roboticsfacility);
        strToResource.put("building_protoss_roboticssupportbay", R.drawable.building_protoss_roboticssupportbay);
        strToResource.put("building_protoss_roboticswarpfacility", R.drawable.building_protoss_roboticswarpfacility);
        strToResource.put("building_protoss_shieldbattery", R.drawable.building_protoss_shieldbattery);
        strToResource.put("building_protoss_solarforge", R.drawable.building_protoss_solarforge);
        strToResource.put("building_protoss_stargate", R.drawable.building_protoss_stargate);
        strToResource.put("building_protoss_starwarpgate", R.drawable.building_protoss_starwarpgate);
        strToResource.put("building_protoss_templararchives", R.drawable.building_protoss_templararchives);
        strToResource.put("building_protoss_twilightcouncil", R.drawable.building_protoss_twilightcouncil);
        strToResource.put("building_protoss_warpgate", R.drawable.building_protoss_warpgate);
        strToResource.put("building_terran_armory", R.drawable.building_terran_armory);
        strToResource.put("building_terran_barracks", R.drawable.building_terran_barracks);
        strToResource.put("building_terran_bunker", R.drawable.building_terran_bunker);
        strToResource.put("building_terran_commandcenter", R.drawable.building_terran_commandcenter);
        strToResource.put("building_terran_deepspacerelay", R.drawable.building_terran_deepspacerelay);
        strToResource.put("building_terran_engineeringbay", R.drawable.building_terran_engineeringbay);
        strToResource.put("building_terran_factory", R.drawable.building_terran_factory);
        strToResource.put("building_terran_fusioncore", R.drawable.building_terran_fusioncore);
        strToResource.put("building_terran_ghostacademy", R.drawable.building_terran_ghostacademy);
        strToResource.put("building_terran_missileturret", R.drawable.building_terran_missileturret);
        strToResource.put("building_terran_planetaryfortress", R.drawable.building_terran_planetaryfortress);
        strToResource.put("building_terran_reactor", R.drawable.building_terran_reactor);
        strToResource.put("building_terran_refinery", R.drawable.building_terran_refinery);
        strToResource.put("building_terran_sensordome", R.drawable.building_terran_sensordome);
        strToResource.put("building_terran_starport", R.drawable.building_terran_starport);
        strToResource.put("building_terran_supplydepot", R.drawable.building_terran_supplydepot);
        strToResource.put("building_terran_surveillancestation", R.drawable.building_terran_surveillancestation);
        strToResource.put("building_terran_techlab", R.drawable.building_terran_techlab);
        strToResource.put("building_zerg_banelingnest", R.drawable.building_zerg_banelingnest);
        strToResource.put("building_zerg_evolutionchamber", R.drawable.building_zerg_evolutionchamber);
        strToResource.put("building_zerg_extractor", R.drawable.building_zerg_extractor);
        strToResource.put("building_zerg_greaterspire", R.drawable.building_zerg_greaterspire);
        strToResource.put("building_zerg_hatchery", R.drawable.building_zerg_hatchery);
        strToResource.put("building_zerg_hive", R.drawable.building_zerg_hive);
        strToResource.put("building_zerg_hydraliskden", R.drawable.building_zerg_hydraliskden);
        strToResource.put("building_zerg_infestationpit", R.drawable.building_zerg_infestationpit);
        strToResource.put("building_zerg_lair", R.drawable.building_zerg_lair);
        strToResource.put("building_zerg_lurkerden", R.drawable.building_zerg_lurkerden);
        strToResource.put("building_zerg_nydusnetwork", R.drawable.building_zerg_nydusnetwork);
        strToResource.put("building_zerg_nydusworm", R.drawable.building_zerg_nydusworm);
        strToResource.put("building_zerg_roachwarren", R.drawable.building_zerg_roachwarren);
        strToResource.put("building_zerg_spawningpool", R.drawable.building_zerg_spawningpool);
        strToResource.put("building_zerg_spinecrawler", R.drawable.building_zerg_spinecrawler);
        strToResource.put("building_zerg_spire", R.drawable.building_zerg_spire);
        strToResource.put("building_zerg_sporecannon", R.drawable.building_zerg_sporecannon);
        strToResource.put("building_zerg_sporecrawler", R.drawable.building_zerg_sporecrawler);
        strToResource.put("building_zerg_ultraliskcavern", R.drawable.building_zerg_ultraliskcavern);
        strToResource.put("techupgrade_terran_combatshield_color", R.drawable.techupgrade_terran_combatshield_color);
        strToResource.put("unit_protoss_adept", R.drawable.unit_protoss_adept);
        strToResource.put("unit_protoss_archon", R.drawable.unit_protoss_archon);
        strToResource.put("unit_protoss_carrier", R.drawable.unit_protoss_carrier);
        strToResource.put("unit_protoss_colossus", R.drawable.unit_protoss_colossus);
        strToResource.put("unit_protoss_darktemplar", R.drawable.unit_protoss_darktemplar);
        strToResource.put("unit_protoss_disruptor", R.drawable.unit_protoss_disruptor);
        strToResource.put("unit_protoss_hightemplar", R.drawable.unit_protoss_hightemplar);
        strToResource.put("unit_protoss_immortal", R.drawable.unit_protoss_immortal);
        strToResource.put("unit_protoss_mothership", R.drawable.unit_protoss_mothership);
        strToResource.put("unit_protoss_mothershipcore", R.drawable.unit_protoss_mothershipcore);
        strToResource.put("unit_protoss_observer", R.drawable.unit_protoss_observer);
        strToResource.put("unit_protoss_oracle", R.drawable.unit_protoss_oracle);
        strToResource.put("unit_protoss_phaseprism", R.drawable.unit_protoss_phaseprism);
        strToResource.put("unit_protoss_phoenix", R.drawable.unit_protoss_phoenix);
        strToResource.put("unit_protoss_probe", R.drawable.unit_protoss_probe);
        strToResource.put("unit_protoss_replicant", R.drawable.unit_protoss_replicant);
        strToResource.put("unit_protoss_sentry", R.drawable.unit_protoss_sentry);
        strToResource.put("unit_protoss_stalker", R.drawable.unit_protoss_stalker);
        strToResource.put("unit_protoss_tempest", R.drawable.unit_protoss_tempest);
        strToResource.put("unit_protoss_warpprism", R.drawable.unit_protoss_warpprism);
        strToResource.put("unit_protoss_warpray", R.drawable.unit_protoss_warpray);
        strToResource.put("unit_protoss_zealot", R.drawable.unit_protoss_zealot);
        strToResource.put("unit_terran_banshee", R.drawable.unit_terran_banshee);
        strToResource.put("unit_terran_battlecruiser", R.drawable.unit_terran_battlecruiser);
        strToResource.put("unit_terran_cyclone", R.drawable.unit_terran_cyclone);
        strToResource.put("unit_terran_dreadnought", R.drawable.unit_terran_dreadnought);
        strToResource.put("unit_terran_dropship", R.drawable.unit_terran_dropship);
        strToResource.put("unit_terran_ghost", R.drawable.unit_terran_ghost);
        strToResource.put("unit_terran_hellion", R.drawable.unit_terran_hellion);
        strToResource.put("unit_terran_hellionbattlemode", R.drawable.unit_terran_hellionbattlemode);
        strToResource.put("unit_terran_liberator", R.drawable.unit_terran_liberator);
        strToResource.put("unit_terran_marauder", R.drawable.unit_terran_marauder);
        strToResource.put("unit_terran_marine", R.drawable.unit_terran_marine);
        strToResource.put("unit_terran_nomad", R.drawable.unit_terran_nomad);
        strToResource.put("unit_terran_raven", R.drawable.unit_terran_raven);
        strToResource.put("unit_terran_reaper", R.drawable.unit_terran_reaper);
        strToResource.put("unit_terran_scv", R.drawable.unit_terran_scv);
        strToResource.put("unit_terran_siegetank", R.drawable.unit_terran_siegetank);
        strToResource.put("unit_terran_targetingdrone", R.drawable.unit_terran_targetingdrone);
        strToResource.put("unit_terran_thor", R.drawable.unit_terran_thor);
        strToResource.put("unit_terran_vikingfighter", R.drawable.unit_terran_vikingfighter);
        strToResource.put("unit_terran_widowmine", R.drawable.unit_terran_widowmine);
        strToResource.put("unit_zerg_baneling", R.drawable.unit_zerg_baneling);
        strToResource.put("unit_zerg_broodlord", R.drawable.unit_zerg_broodlord);
        strToResource.put("unit_zerg_corruptor", R.drawable.unit_zerg_corruptor);
        strToResource.put("unit_zerg_drone", R.drawable.unit_zerg_drone);
        strToResource.put("unit_zerg_hydralisk", R.drawable.unit_zerg_hydralisk);
        strToResource.put("unit_zerg_infestor", R.drawable.unit_zerg_infestor);
        strToResource.put("unit_zerg_larva", R.drawable.unit_zerg_larva);
        strToResource.put("unit_zerg_lurker", R.drawable.unit_zerg_lurker);
        strToResource.put("unit_zerg_mutalisk", R.drawable.unit_zerg_mutalisk);
        strToResource.put("unit_zerg_omegalisk", R.drawable.unit_zerg_omegalisk);
        strToResource.put("unit_zerg_overlord", R.drawable.unit_zerg_overlord);
        strToResource.put("unit_zerg_overlordtransport", R.drawable.unit_zerg_overlordtransport);
        strToResource.put("unit_zerg_overseer", R.drawable.unit_zerg_overseer);
        strToResource.put("unit_zerg_queen", R.drawable.unit_zerg_queen);
        strToResource.put("unit_zerg_ravager", R.drawable.unit_zerg_ravager);
        strToResource.put("unit_zerg_roach", R.drawable.unit_zerg_roach);
        strToResource.put("unit_zerg_swarmhost", R.drawable.unit_zerg_swarmhost);
        strToResource.put("unit_zerg_ultralisk", R.drawable.unit_zerg_ultralisk);
        strToResource.put("unit_zerg_viper", R.drawable.unit_zerg_viper);
        strToResource.put("unit_zerg_zergling", R.drawable.unit_zerg_zergling);
        strToResource.put("upgrade_protoss_adeptshieldupgrade", R.drawable.upgrade_protoss_adeptshieldupgrade);
        strToResource.put("upgrade_protoss_airarmorlevel0", R.drawable.upgrade_protoss_airarmorlevel0);
        strToResource.put("upgrade_protoss_airarmorlevel1", R.drawable.upgrade_protoss_airarmorlevel1);
        strToResource.put("upgrade_protoss_airarmorlevel2", R.drawable.upgrade_protoss_airarmorlevel2);
        strToResource.put("upgrade_protoss_airarmorlevel3", R.drawable.upgrade_protoss_airarmorlevel3);
        strToResource.put("upgrade_protoss_airweaponslevel0", R.drawable.upgrade_protoss_airweaponslevel0);
        strToResource.put("upgrade_protoss_airweaponslevel1", R.drawable.upgrade_protoss_airweaponslevel1);
        strToResource.put("upgrade_protoss_airweaponslevel2", R.drawable.upgrade_protoss_airweaponslevel2);
        strToResource.put("upgrade_protoss_airweaponslevel3", R.drawable.upgrade_protoss_airweaponslevel3);
        strToResource.put("upgrade_protoss_extendedthermallance", R.drawable.upgrade_protoss_extendedthermallance);
        strToResource.put("upgrade_protoss_fluxvanes", R.drawable.upgrade_protoss_fluxvanes);
        strToResource.put("upgrade_protoss_graviticbooster", R.drawable.upgrade_protoss_graviticbooster);
        strToResource.put("upgrade_protoss_graviticdrive", R.drawable.upgrade_protoss_graviticdrive);
        strToResource.put("upgrade_protoss_gravitoncatapult", R.drawable.upgrade_protoss_gravitoncatapult);
        strToResource.put("upgrade_protoss_groundarmorlevel0", R.drawable.upgrade_protoss_groundarmorlevel0);
        strToResource.put("upgrade_protoss_groundarmorlevel1", R.drawable.upgrade_protoss_groundarmorlevel1);
        strToResource.put("upgrade_protoss_groundarmorlevel2", R.drawable.upgrade_protoss_groundarmorlevel2);
        strToResource.put("upgrade_protoss_groundarmorlevel3", R.drawable.upgrade_protoss_groundarmorlevel3);
        strToResource.put("upgrade_protoss_groundweaponslevel0", R.drawable.upgrade_protoss_groundweaponslevel0);
        strToResource.put("upgrade_protoss_groundweaponslevel1", R.drawable.upgrade_protoss_groundweaponslevel1);
        strToResource.put("upgrade_protoss_groundweaponslevel2", R.drawable.upgrade_protoss_groundweaponslevel2);
        strToResource.put("upgrade_protoss_groundweaponslevel3", R.drawable.upgrade_protoss_groundweaponslevel3);
        strToResource.put("upgrade_protoss_khaydarinamulet", R.drawable.upgrade_protoss_khaydarinamulet);
        strToResource.put("upgrade_protoss_phoenixrange", R.drawable.upgrade_protoss_phoenixrange);
        strToResource.put("upgrade_protoss_resonatingglaives", R.drawable.upgrade_protoss_resonatingglaives);
        strToResource.put("upgrade_protoss_shieldslevel0", R.drawable.upgrade_protoss_shieldslevel0);
        strToResource.put("upgrade_protoss_shieldslevel1", R.drawable.upgrade_protoss_shieldslevel1);
        strToResource.put("upgrade_protoss_shieldslevel2", R.drawable.upgrade_protoss_shieldslevel2);
        strToResource.put("upgrade_protoss_shieldslevel3", R.drawable.upgrade_protoss_shieldslevel3);
        strToResource.put("upgrade_terran_acidexplosionimmunity", R.drawable.upgrade_terran_acidexplosionimmunity);
        strToResource.put("upgrade_terran_advanceballistics", R.drawable.upgrade_terran_advanceballistics);
        strToResource.put("upgrade_terran_behemothreactor", R.drawable.upgrade_terran_behemothreactor);
        strToResource.put("upgrade_terran_buildingarmor", R.drawable.upgrade_terran_buildingarmor);
        strToResource.put("upgrade_terran_caduceusreactor", R.drawable.upgrade_terran_caduceusreactor);
        strToResource.put("upgrade_terran_corvidreactor", R.drawable.upgrade_terran_corvidreactor);
        strToResource.put("upgrade_terran_cyclonerangeupgrade", R.drawable.upgrade_terran_cyclonerangeupgrade);
        strToResource.put("upgrade_terran_dreadnoughtagmode", R.drawable.upgrade_terran_dreadnoughtagmode);
        strToResource.put("upgrade_terran_durablematerials", R.drawable.upgrade_terran_durablematerials);
        strToResource.put("upgrade_terran_hisecautotracking", R.drawable.upgrade_terran_hisecautotracking);
        strToResource.put("upgrade_terran_infantryarmorlevel0", R.drawable.upgrade_terran_infantryarmorlevel0);
        strToResource.put("upgrade_terran_infantryarmorlevel1", R.drawable.upgrade_terran_infantryarmorlevel1);
        strToResource.put("upgrade_terran_infantryarmorlevel2", R.drawable.upgrade_terran_infantryarmorlevel2);
        strToResource.put("upgrade_terran_infantryarmorlevel3", R.drawable.upgrade_terran_infantryarmorlevel3);
        strToResource.put("upgrade_terran_infantryweaponslevel0", R.drawable.upgrade_terran_infantryweaponslevel0);
        strToResource.put("upgrade_terran_infantryweaponslevel1", R.drawable.upgrade_terran_infantryweaponslevel1);
        strToResource.put("upgrade_terran_infantryweaponslevel2", R.drawable.upgrade_terran_infantryweaponslevel2);
        strToResource.put("upgrade_terran_infantryweaponslevel3", R.drawable.upgrade_terran_infantryweaponslevel3);
        strToResource.put("upgrade_terran_infernalpreigniter", R.drawable.upgrade_terran_infernalpreigniter);
        strToResource.put("upgrade_terran_medivacemergencythrusters", R.drawable.upgrade_terran_medivacemergencythrusters);
        strToResource.put("upgrade_terran_mobiusreactor", R.drawable.upgrade_terran_mobiusreactor);
        strToResource.put("upgrade_terran_neosteelframe", R.drawable.upgrade_terran_neosteelframe);
        strToResource.put("upgrade_terran_researchdrillingclaws", R.drawable.upgrade_terran_researchdrillingclaws);
        strToResource.put("upgrade_terran_shipplatinglevel0", R.drawable.upgrade_terran_shipplatinglevel0);
        strToResource.put("upgrade_terran_shipplatinglevel1", R.drawable.upgrade_terran_shipplatinglevel1);
        strToResource.put("upgrade_terran_shipplatinglevel2", R.drawable.upgrade_terran_shipplatinglevel2);
        strToResource.put("upgrade_terran_shipplatinglevel3", R.drawable.upgrade_terran_shipplatinglevel3);
        strToResource.put("upgrade_terran_shipweaponslevel0", R.drawable.upgrade_terran_shipweaponslevel0);
        strToResource.put("upgrade_terran_shipweaponslevel1", R.drawable.upgrade_terran_shipweaponslevel1);
        strToResource.put("upgrade_terran_shipweaponslevel2", R.drawable.upgrade_terran_shipweaponslevel2);
        strToResource.put("upgrade_terran_shipweaponslevel3", R.drawable.upgrade_terran_shipweaponslevel3);
        strToResource.put("upgrade_terran_vehicleplatinglevel0", R.drawable.upgrade_terran_vehicleplatinglevel0);
        strToResource.put("upgrade_terran_vehicleplatinglevel1", R.drawable.upgrade_terran_vehicleplatinglevel1);
        strToResource.put("upgrade_terran_vehicleplatinglevel2", R.drawable.upgrade_terran_vehicleplatinglevel2);
        strToResource.put("upgrade_terran_vehicleplatinglevel3", R.drawable.upgrade_terran_vehicleplatinglevel3);
        strToResource.put("upgrade_terran_vehicleweaponslevel0", R.drawable.upgrade_terran_vehicleweaponslevel0);
        strToResource.put("upgrade_terran_vehicleweaponslevel1", R.drawable.upgrade_terran_vehicleweaponslevel1);
        strToResource.put("upgrade_terran_vehicleweaponslevel2", R.drawable.upgrade_terran_vehicleweaponslevel2);
        strToResource.put("upgrade_terran_vehicleweaponslevel3", R.drawable.upgrade_terran_vehicleweaponslevel3);
        strToResource.put("upgrade_terran_weaponrefit", R.drawable.upgrade_terran_weaponrefit);
        strToResource.put("upgrade_zerg_airattacks_level0", R.drawable.upgrade_zerg_airattacks_level0);
        strToResource.put("upgrade_zerg_airattacks_level1", R.drawable.upgrade_zerg_airattacks_level1);
        strToResource.put("upgrade_zerg_airattacks_level2", R.drawable.upgrade_zerg_airattacks_level2);
        strToResource.put("upgrade_zerg_airattacks_level3", R.drawable.upgrade_zerg_airattacks_level3);
        strToResource.put("upgrade_zerg_anabolicsynthesis", R.drawable.upgrade_zerg_anabolicsynthesis);
        strToResource.put("upgrade_zerg_centrifugalhooks", R.drawable.upgrade_zerg_centrifugalhooks);
        strToResource.put("upgrade_zerg_chitinousplating", R.drawable.upgrade_zerg_chitinousplating);
        strToResource.put("upgrade_zerg_evolveincreasedlocustlifetime", R.drawable.upgrade_zerg_evolveincreasedlocustlifetime);
        strToResource.put("upgrade_zerg_evolvemuscularaugments", R.drawable.upgrade_zerg_evolvemuscularaugments);
        strToResource.put("upgrade_zerg_flyercarapace_level0", R.drawable.upgrade_zerg_flyercarapace_level0);
        strToResource.put("upgrade_zerg_flyercarapace_level1", R.drawable.upgrade_zerg_flyercarapace_level1);
        strToResource.put("upgrade_zerg_flyercarapace_level2", R.drawable.upgrade_zerg_flyercarapace_level2);
        strToResource.put("upgrade_zerg_flyercarapace_level3", R.drawable.upgrade_zerg_flyercarapace_level3);
        strToResource.put("upgrade_zerg_frenzy", R.drawable.upgrade_zerg_frenzy);
        strToResource.put("upgrade_zerg_glialreconstitution", R.drawable.upgrade_zerg_glialreconstitution);
        strToResource.put("upgrade_zerg_groovedspines", R.drawable.upgrade_zerg_groovedspines);
        strToResource.put("upgrade_zerg_groundcarapace_level0", R.drawable.upgrade_zerg_groundcarapace_level0);
        strToResource.put("upgrade_zerg_groundcarapace_level1", R.drawable.upgrade_zerg_groundcarapace_level1);
        strToResource.put("upgrade_zerg_groundcarapace_level2", R.drawable.upgrade_zerg_groundcarapace_level2);
        strToResource.put("upgrade_zerg_groundcarapace_level3", R.drawable.upgrade_zerg_groundcarapace_level3);
        strToResource.put("upgrade_zerg_hardenedcarapace", R.drawable.upgrade_zerg_hardenedcarapace);
        strToResource.put("upgrade_zerg_hotsgroovedspines", R.drawable.upgrade_zerg_hotsgroovedspines);
        strToResource.put("upgrade_zerg_hotsmetabolicboost", R.drawable.upgrade_zerg_hotsmetabolicboost);
        strToResource.put("upgrade_zerg_hotstunnelingclaws", R.drawable.upgrade_zerg_hotstunnelingclaws);
        strToResource.put("upgrade_zerg_meleeattacks_level0", R.drawable.upgrade_zerg_meleeattacks_level0);
        strToResource.put("upgrade_zerg_meleeattacks_level1", R.drawable.upgrade_zerg_meleeattacks_level1);
        strToResource.put("upgrade_zerg_meleeattacks_level2", R.drawable.upgrade_zerg_meleeattacks_level2);
        strToResource.put("upgrade_zerg_meleeattacks_level3", R.drawable.upgrade_zerg_meleeattacks_level3);
        strToResource.put("upgrade_zerg_metabolicboost", R.drawable.upgrade_zerg_metabolicboost);
        strToResource.put("upgrade_zerg_missileattacks_level0", R.drawable.upgrade_zerg_missileattacks_level0);
        strToResource.put("upgrade_zerg_missileattacks_level1", R.drawable.upgrade_zerg_missileattacks_level1);
        strToResource.put("upgrade_zerg_missileattacks_level2", R.drawable.upgrade_zerg_missileattacks_level2);
        strToResource.put("upgrade_zerg_missileattacks_level3", R.drawable.upgrade_zerg_missileattacks_level3);
        strToResource.put("upgrade_zerg_muscularaugments", R.drawable.upgrade_zerg_muscularaugments);
        strToResource.put("upgrade_zerg_pathogenglands", R.drawable.upgrade_zerg_pathogenglands);
        strToResource.put("upgrade_zerg_pneumatizedcarapace", R.drawable.upgrade_zerg_pneumatizedcarapace);
        strToResource.put("upgrade_zerg_rapidincubation", R.drawable.upgrade_zerg_rapidincubation);
        strToResource.put("upgrade_zerg_rapidregeneration", R.drawable.upgrade_zerg_rapidregeneration);
        strToResource.put("upgrade_zerg_ventralsacs", R.drawable.upgrade_zerg_ventralsacs);
    }

    @DrawableRes
    public static int getDrawableId(String value) {
        if(strToResource == null) {
            initialize();
        }
        if(strToResource.containsKey(value)) {
            return strToResource.get(value);
        } else {
            return 0;
        }
    }

    public static final int NO_TIMING = -1;

    public String population;
    public String strTiming;
    public int onFinish = -1;
    public long deltaTiming;
    public int timing = NO_TIMING;
    public String name;
    public int resourceIcon;
    public int count = 1;
    public String details;
    View view;

    public SC2Action() {

    }

    @Override
    public String toString() {
        return population+" "+timing+" "+name+" "+details;
    }
}
