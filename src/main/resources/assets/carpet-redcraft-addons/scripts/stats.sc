global_custom_stats = ['animals_bred','clean_armor','clean_banner','open_barrel','bell_ring','eat_cake_slice','fill_cauldron','open_chest','damage_absorbed','damage_blocked_by_shield','damage_dealt','damage_dealt_absorbed','damage_dealt_resisted','damage_resisted','damage_taken','inspect_dispenser','climb_one_cm','crouch_one_cm','fall_one_cm','fly_one_cm','sprint_one_cm','swim_one_cm','walk_one_cm','walk_on_water_one_cm','walk_under_water_one_cm','boat_one_cm','aviate_one_cm','horse_one_cm','minecart_one_cm','pig_one_cm','strider_one_cm','inspect_dropper','open_enderchest','fish_caught','leave_game','inspect_hopper','interact_with_anvil','interact_with_beacon','interact_with_blast_furnace','interact_with_brewingstand','interact_with_campfire','interact_with_cartography_table','interact_with_crafting_table','interact_with_furnace','interact_with_gridstone','interact_with_lectern','interact_with_loom','interact_with_smithing_table','interact_with_smoker','interact_with_stonecutter','drop','enchant_item','jump','mob_kills','play_record','play_noteblock','tune_noteblock','deaths','pot_flower','player_kills','raid_trigger','raid_win','clean_shulker_box','open_shulker_box','sneak_time','talked_to_villager','target_hit','play_one_minute','time_since_death','time_since_rest','sleep_in_bed','traded_with_villager','trigger_trapped_chest','use_cauldron'];
__config() -> {
    'stay_loaded' -> true,
    'scope' -> 'global',
	'commands' -> {
		//'<statistic>' -> '_show_stat',
		//'hide' -> '_hide',
		//'show' -> '_show',
		'' -> '_toggle',
        'broken <itemname>' -> _(in) -> _show_stat('minecraft.broken:minecraft.' + in),
        'custom <customstats>' -> _(cs) -> _show_stat('minecraft.custom:minecraft.' + cs),
        'killed <entityname>' -> _(en) -> _show_stat('minecraft.killed:minecraft.' + en),
        'mined <blockname>' -> _(bn) -> _show_stat('minecraft.mined:minecraft.' + bn),
        'picked_up <itemname>' -> _(in) -> _show_stat('minecraft.picked_up:minecraft.' + in),
        'used <itemname>' -> _(in) -> _show_stat('minecraft.used:minecraft.' + in)
	},
    'arguments' -> {
       //'statistic' -> {'type' -> 'criterion'},
       'blockname' -> {'type' -> 'term', 'suggest' -> block_list()},
       'itemname' -> {'type' -> 'term', 'suggest' -> item_list()},
       'entityname' -> {'type' -> 'term', 'suggest' -> entity_types()},
       'customstats' -> {'type' -> 'term', 'suggest' -> global_custom_stats}
    }
};

scoreboard_add('redcraft.stats');

global_show = false;
_show() -> (
    scoreboard_display('sidebar', 'redcraft.stats');
    global_show = true
);
_hide() -> (
    scoreboard_display('sidebar', null);
    global_show = false
);
_toggle() -> if(global_show = !global_show,
    scoreboard_display('sidebar', 'redcraft.stats'),
    scoreboard_display('sidebar', null)
);

_show_stat(stat) -> (
    _hide();
    if((stats = stat ~ '^minecraft.(\\w+):minecraft.(\\w+)$') == null, return());
    scoreboard_remove('redcraft.stats');
    scoreboard_add('redcraft.stats', stat);
    // save();
    run(str('scoreboard objectives modify redcraft.stats displayname {"text":"%s","color":"#ff0000"}',
        if(stats:0 != 'custom', title(stats:0+' '), '') + title(replace(stats:1, '_', ' '))
    ));
    nbt = parse_nbt(nbt_storage('redcraft:players'));
    for(nbt,scoreboard('redcraft.stats', nbt:_, offline_statistic(_, stats:0, stats:1)));
    _show()
);

__on_player_connects(player) -> (
    if(player~'gamemode_id'==3,return());
    nbt = parse_nbt(nbt_storage('redcraft:players'));
    if(put(nbt, player~'uuid', player~'name') != null,
        nbt_storage('redcraft:players', encode_nbt(nbt))
    )
)
