package mekanism.common;

import mekanism.api.text.ILangEntry;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.Util;

public enum MekanismLang implements ILangEntry {
    //Vanilla lang strings we use, for purposes of not having to have them copy pasted all over the place
    INVENTORY("container.inventory"),
    REPAIR_COST("container.repair.cost"),
    REPAIR_EXPENSIVE("container.repair.expensive"),
    //Gui lang strings
    MEKANISM("constants", "mod_name"),
    DEBUG_TITLE("constants", "debug_title"),
    LOG_FORMAT("constants", "log_format"),
    FORGE("constants", "forge"),
    IC2("constants", "ic2"),
    ERROR("constants", "error"),
    ALPHA_WARNING("constants", "alpha_warning"),
    ALPHA_WARNING_HERE("constants", "alpha_warning.here"),
    //Equipment
    HEAD("equipment", "head"),
    BODY("equipment", "body"),
    LEGS("equipment", "legs"),
    FEET("equipment", "feet"),
    MAINHAND("equipment", "mainhand"),
    OFFHAND("equipment", "offhand"),
    //Compass Directions
    NORTH_SHORT("direction", "north.short"),
    SOUTH_SHORT("direction", "south.short"),
    WEST_SHORT("direction", "west.short"),
    EAST_SHORT("direction", "east.short"),
    //Multiblock Stuff
    MULTIBLOCK_INVALID_FRAME("multiblock", "invalid_frame"),
    MULTIBLOCK_INVALID_INNER("multiblock", "invalid_inner"),
    MULTIBLOCK_INVALID_CONTROLLER_CONFLICT("multiblock", "invalid_controller_conflict"),
    MULTIBLOCK_INVALID_NO_CONTROLLER("multiblock", "invalid_no_controller"),
    //SPS
    SPS("sps", "sps"),
    SPS_INVALID_DISCONNECTED_COIL("sps", "invalid_disconnected_coil"),
    SPS_PORT_MODE("sps", "port_mode"),
    SPS_ENERGY_INPUT("sps", "energy_input"),
    //Boiler
    BOILER_INVALID_AIR_POCKETS("boiler", "invalid_air_pockets"),
    BOILER_INVALID_EXTRA_DISPERSER("boiler", "invalid_extra_disperser"),
    BOILER_INVALID_MISSING_DISPERSER("boiler", "invalid_missing_disperser"),
    BOILER_INVALID_NO_DISPERSER("boiler", "invalid_no_disperser"),
    BOILER_INVALID_SUPERHEATING("boiler", "invalid_superheating"),
    //Conversion
    CONVERSION_ENERGY("conversion", "energy"),
    CONVERSION_GAS("conversion", "gas"),
    CONVERSION_INFUSION("conversion", "infusion"),
    //QIO stuff
    SET_FREQUENCY("qio", "set_frequency"),
    QIO_FREQUENCY_SELECT("qio", "qio_frequency_select"),
    QIO_ITEMS_DETAIL("qio", "items_detail"),
    QIO_TYPES_DETAIL("qio", "types_detail"),
    QIO_ITEMS("qio", "items"),
    QIO_TYPES("qio", "types"),
    QIO_TRIGGER_COUNT("qio", "trigger_count"),
    QIO_STORED_COUNT("qio", "stored_count"),
    QIO_ITEM_TYPE_UNDEFINED("qio", "item_type_undefined"),
    QIO_IMPORT_WITHOUT_FILTER("qio", "import_without_filter"),
    QIO_EXPORT_WITHOUT_FILTER("qio", "export_without_filter"),
    QIO_COMPENSATE_TOOLTIP("qio", "compensate_tooltip"),
    LIST_SORT_COUNT("qio", "sort_count"),
    LIST_SORT_NAME("qio", "sort_name"),
    LIST_SORT_MOD("qio", "sort_mod"),
    LIST_SORT_NAME_DESC("qio", "sort_name_desc"),
    LIST_SORT_COUNT_DESC("qio", "sort_count_desc"),
    LIST_SORT_MOD_DESC("qio", "sort_mod_desc"),
    LIST_SORT_ASCENDING_DESC("qio", "sort_ascending_desc"),
    LIST_SORT_DESCENDING_DESC("qio", "sort_descending_desc"),
    LIST_SEARCH("qio", "list_search"),
    LIST_SORT("qio", "list_sort"),
    //JEI
    JEI_AMOUNT_WITH_CAPACITY("tooltip", "jei.amount.with.capacity"),
    //Key
    KEY_HAND_MODE("key", "mode"),
    KEY_HEAD_MODE("key", "head_mode"),
    KEY_CHEST_MODE("key", "chest_mode"),
    KEY_FEET_MODE("key", "feet_mode"),
    KEY_DETAILS_MODE("key", "details"),
    KEY_DESCRIPTION_MODE("key", "description"),
    KEY_MODULE_TWEAKER("key", "module_tweaker"),
    KEY_BOOST("key", "key_boost"),
    KEY_HUD("key", "key_hud"),
    //Holiday
    HOLIDAY_BORDER("holiday", "border"),
    HOLIDAY_SIGNATURE("holiday", "signature"),
    CHRISTMAS_LINE_ONE("holiday", "christmas.1"),
    CHRISTMAS_LINE_TWO("holiday", "christmas.2"),
    CHRISTMAS_LINE_THREE("holiday", "christmas.3"),
    CHRISTMAS_LINE_FOUR("holiday", "christmas.4"),
    NEW_YEAR_LINE_ONE("holiday", "new_year.1"),
    NEW_YEAR_LINE_TWO("holiday", "new_year.2"),
    NEW_YEAR_LINE_THREE("holiday", "new_year.3"),
    MAY_4_LINE_ONE("holiday", "may_4.1"),
    //Generic
    GENERIC_WITH_COMMA("generic", "with_comma"),
    GENERIC_STORED("generic", "stored"),
    GENERIC_STORED_MB("generic", "stored.mb"),
    GENERIC_MB("generic", "mb"),
    GENERIC_PRE_COLON("generic", "pre_colon"),
    GENERIC_SQUARE_BRACKET("generic", "square_bracket"),
    GENERIC_PARENTHESIS("generic", "parenthesis"),
    GENERIC_WITH_PARENTHESIS("generic", "with_parenthesis"),
    GENERIC_WITH_TWO_PARENTHESIS("generic", "with_two_parenthesis"),
    GENERIC_FRACTION("generic", "fraction"),
    GENERIC_TRANSFER("generic", "transfer"),
    GENERIC_PER_TICK("generic", "per_tick"),
    GENERIC_PRE_STORED("generic", "pre_pre_colon"),
    GENERIC_BLOCK_POS("generic", "block_pos"),
    GENERIC_HEX("generic", "hex"),
    //Hold for
    HOLD_FOR_DETAILS("tooltip", "hold_for_details"),
    HOLD_FOR_DESCRIPTION("tooltip", "hold_for_description"),
    HOLD_FOR_MODULES("tooltip", "hold_for_modules"),
    HOLD_FOR_SUPPORTED_ITEMS("tooltip", "hold_for_supported_items"),
    //Commands
    COMMAND_CHUNK("command", "chunk"),
    COMMAND_CHUNK_WATCH("command", "chunk.watch"),
    COMMAND_CHUNK_UNWATCH("command", "chunk.unwatch"),
    COMMAND_CHUNK_CLEAR("command", "chunk.clear"),
    COMMAND_CHUNK_FLUSH("command", "chunk.flush"),
    COMMAND_CHUNK_LOADED("command", "chunk.loaded"),
    COMMAND_CHUNK_UNLOADED("command", "chunk.unloaded"),
    COMMAND_DEBUG("command", "debug"),
    COMMAND_TEST_RULES("command", "testrules"),
    COMMAND_TP("command", "tp"),
    COMMAND_TPOP("command", "tpop"),
    COMMAND_TPOP_EMPTY("command", "tpop.empty"),
    COMMAND_RADIATION_ADD("command", "radiation.add"),
    COMMAND_RADIATION_GET("command", "radiation.get"),
    COMMAND_RADIATION_CLEAR("command", "radiation.clear"),
    COMMAND_RADIATION_REMOVE_ALL("command", "radiation.remove_all"),
    //Transmission types
    TRANSMISSION_TYPE_ENERGY("transmission", "energy"),
    TRANSMISSION_TYPE_FLUID("transmission", "fluids"),
    TRANSMISSION_TYPE_GAS("transmission", "gases"),
    TRANSMISSION_TYPE_INFUSION("transmission", "infuse_types"),
    TRANSMISSION_TYPE_PIGMENT("transmission", "pigments"),
    TRANSMISSION_TYPE_SLURRY("transmission", "slurries"),
    TRANSMISSION_TYPE_ITEM("transmission", "items"),
    TRANSMISSION_TYPE_HEAT("transmission", "heat"),
    //Tooltip stuff
    MODE("tooltip", "mode"),
    FIRE_MODE("tooltip", "fire_mode"),
    BUCKET_MODE("tooltip", "bucket_mode"),
    STORED_ENERGY("tooltip", "stored_energy"),
    STORED("tooltip", "stored"),
    STORED_MB_PERCENTAGE("tooltip", "stored_mb_percentage"),
    ITEM_AMOUNT("tooltip", "item_amount"),
    FLOWING("tooltip", "flowing"),
    INVALID("tooltip", "invalid"),
    HAS_INVENTORY("tooltip", "inventory"),
    NO_GAS("tooltip", "no_gas"),
    FREE_RUNNERS_MODE("tooltip", "mode.free_runners"),
    JETPACK_MODE("tooltip", "mode.jetpack"),
    SCUBA_TANK_MODE("tooltip", "mode.scuba_tank"),
    FREE_RUNNERS_STORED("tooltip", "stored.free_runners"),
    FLAMETHROWER_STORED("tooltip", "stored.flamethrower"),
    JETPACK_STORED("tooltip", "stored.jetpack"),
    //Gui stuff
    HEIGHT("gui", "height"),
    WIDTH("gui", "width"),
    PROGRESS("gui", "progress"),
    PROCESS_RATE("gui", "process_rate"),
    PROCESS_RATE_MB("gui", "process_rate_mb"),
    TICKS_REQUIRED("gui", "ticks_required"),
    MIN("gui", "min"),
    MAX("gui", "max"),
    INFINITE("gui", "infinite"),
    NONE("gui", "none"),
    EMPTY("gui", "empty"),
    MAX_OUTPUT("gui", "max_output"),
    STORING("gui", "storing"),
    DISSIPATED_RATE("gui", "dissipated"),
    TRANSFERRED_RATE("gui", "transferred_rate"),
    FUEL("gui", "fuel"),
    VOLUME("gui", "volume"),
    NO_FLUID("gui", "no_fluid"),
    CHEMICAL("gui", "chemical"),
    GAS("gui", "gas"),
    INFUSE_TYPE("gui", "infuse_type"),
    PIGMENT("gui", "pigment"),
    SLURRY("gui", "slurry"),
    LIQUID("gui", "liquid"),
    UNIT("gui", "unit"),
    USING("gui", "using"),
    NEEDED("gui", "needed"),
    NEEDED_PER_TICK("gui", "needed_per_tick"),
    FINISHED("gui", "finished"),
    NO_RECIPE("gui", "no_recipe"),
    EJECT("gui", "eject"),
    NO_DELAY("gui", "no_delay"),
    DELAY("gui", "delay"),
    ENERGY("gui", "energy"),
    RESISTIVE_HEATER_USAGE("gui", "usage"),
    DYNAMIC_TANK("gui", "dynamic_tank"),
    CHEMICAL_DISSOLUTION_CHAMBER_SHORT("gui", "chemical_dissolution_chamber.short"),
    CHEMICAL_INFUSER_SHORT("gui", "chemical_infuser.short"),
    MOVE_UP("gui", "move_up"),
    MOVE_DOWN("gui", "move_down"),
    SET("gui", "set"),
    TRUE("gui", "true"),
    FALSE("gui", "false"),
    CLOSE("gui", "close"),
    RADIATION_DOSE("gui", "radiation_dose"),
    RADIATION_EXPOSURE("gui", "radiation_exposure"),
    COLOR_PICKER("gui", "color_picker"),
    RGB("gui", "rgb"),
    HELMET_OPTIONS("gui", "helmet_options"),
    HUD_OVERLAY("gui", "hud_overlay"),
    OPACITY("gui", "opacity"),
    DEFAULT("gui", "default"),
    WARNING("gui", "warning"),
    DANGER("gui", "danger"),
    COMPASS("gui", "compass"),
    //Laser Amplifier
    ENTITY_DETECTION("laser_amplifier", "entity_detection"),
    ENERGY_CONTENTS("laser_amplifier", "energy_contents"),
    REDSTONE_OUTPUT("laser_amplifier", "redstone_output"),
    //Frequency
    FREQUENCY("frequency", "format"),
    NO_FREQUENCY("frequency", "none"),
    FREQUENCY_DELETE_CONFIRM("frequency", "delete_confirm"),
    //Owner
    NOW_OWN("owner", "now_own"),
    OWNER("owner", "format"),
    NO_OWNER("owner", "none"),
    //Tab
    MAIN_TAB("tab", "main"),
    //Evaporation
    EVAPORATION_HEIGHT("evaporation", "height"),
    FLUID_PRODUCTION("evaporation", "fluid_production"),
    EVAPORATION_PLANT("evaporation", "evaporation_plant"),
    //Configuration
    TRANSPORTER_CONFIG("configuration", "transporter"),
    SIDE_CONFIG("configuration", "side"),
    STRICT_INPUT("configuration", "strict_input"),
    STRICT_INPUT_ENABLED("configuration", "strict_input.enabled"),
    CONFIG_TYPE("configuration", "config_type"),
    NO_EJECT("configuration", "no_eject"),
    SLOTS("configuration", "slots"),
    //Auto
    AUTO_PULL("auto", "pull"),
    AUTO_EJECT("auto", "eject"),
    AUTO_SORT("auto", "sort"),
    //Gas mode
    IDLE("gas_mode", "idle"),
    DUMPING_EXCESS("gas_mode", "dumping_excess"),
    DUMPING("gas_mode", "dumping"),
    //Dictionary
    DICTIONARY_KEY("dictionary", "key"),
    DICTIONARY_NO_KEY("dictionary", "no_key"),
    DICTIONARY_KEYS_FOUND("dictionary", "keys_found"),
    DICTIONARY_TAG_TYPE("dictionary", "tag_type"),
    DICTIONARY_ITEM("dictionary", "item"),
    DICTIONARY_ITEM_DESC("dictionary", "item.desc"),
    DICTIONARY_BLOCK("dictionary", "block"),
    DICTIONARY_BLOCK_DESC("dictionary", "block.desc"),
    DICTIONARY_FLUID("dictionary", "fluid"),
    DICTIONARY_FLUID_DESC("dictionary", "fluid.desc"),
    DICTIONARY_ENTITY_TYPE("dictionary", "entity_type"),
    DICTIONARY_ENTITY_TYPE_DESC("dictionary", "entity_type.desc"),
    DICTIONARY_GAS("dictionary", "gas"),
    DICTIONARY_GAS_DESC("dictionary", "gas.desc"),
    DICTIONARY_INFUSE_TYPE("dictionary", "infuse_type"),
    DICTIONARY_INFUSE_TYPE_DESC("dictionary", "infuse_type.desc"),
    DICTIONARY_PIGMENT("dictionary", "pigment"),
    DICTIONARY_PIGMENT_DESC("dictionary", "pigment.desc"),
    DICTIONARY_SLURRY("dictionary", "slurry"),
    DICTIONARY_SLURRY_DESC("dictionary", "slurry.desc"),
    //Oredictionificator
    LAST_ITEM("oredictionificator", "last_item"),
    NEXT_ITEM("oredictionificator", "next_item"),
    //Status
    STATUS("status", "format"),
    STATUS_OK("status", "ok"),
    //Fluid container
    FLUID_CONTAINER_BOTH("fluid_container", "both"),
    FLUID_CONTAINER_FILL("fluid_container", "fill"),
    FLUID_CONTAINER_EMPTY("fluid_container", "empty"),
    //Boolean values
    YES("boolean", "yes"),
    NO("boolean", "no"),
    ON("boolean", "on"),
    OFF("boolean", "off"),
    INPUT("boolean", "input"),
    OUTPUT("boolean", "output"),
    ACTIVE("boolean", "active"),
    DISABLED("boolean", "disabled"),
    ON_CAPS("boolean", "on_caps"),
    OFF_CAPS("boolean", "off_caps"),
    //Capacity
    CAPACITY("capacity", "generic"),
    CAPACITY_ITEMS("capacity", "items"),
    CAPACITY_MB("capacity", "mb"),
    CAPACITY_PER_TICK("capacity", "per_tick"),
    CAPACITY_MB_PER_TICK("capacity", "mb.per_tick"),
    //Cardboard box
    BLOCK_DATA("cardboard_box", "block_data"),
    BLOCK("cardboard_box", "block"),
    TILE("cardboard_box", "tile"),
    //Crafting Formula
    INGREDIENTS("crafting_formula", "ingredients"),
    ENCODED("crafting_formula", "encoded"),
    //Multiblock
    MULTIBLOCK_INCOMPLETE("multiblock", "incomplete"),
    MULTIBLOCK_FORMED("multiblock", "formed"),
    MULTIBLOCK_CONFLICT("multiblock", "conflict"),
    MULTIBLOCK_FORMED_CHAT("multiblock", "formed.chat"),
    //Transmitter tooltips
    UNIVERSAL("transmitter", "universal"),
    ITEMS("transmitter", "items"),
    BLOCKS("transmitter", "blocks"),
    FLUIDS("transmitter", "fluids"),
    GASES("transmitter", "gases"),
    HEAT("transmitter", "heat"),
    CONDUCTION("transmitter", "conduction"),
    INSULATION("transmitter", "insulation"),
    HEAT_CAPACITY("transmitter", "heat_capacity"),
    CAPABLE_OF_TRANSFERRING("transmitter", "capable_of_transferring"),
    DIVERSION_CONTROL_DISABLED("transmitter", "control.disabled.desc"),
    DIVERSION_CONTROL_HIGH("transmitter", "control.high.desc"),
    DIVERSION_CONTROL_LOW("transmitter", "control.low.desc"),
    TOGGLE_DIVERTER("transmitter", "configurator.toggle_diverter"),
    PUMP_RATE("transmitter", "pump_rate"),
    PUMP_RATE_MB("transmitter", "pump_rate.mb"),
    SPEED("transmitter", "speed"),
    //Condensentrator
    CONDENSENTRATOR_TOGGLE("condensentrator", "toggle"),
    CONDENSENTRATING("condensentrator", "condensentrating"),
    DECONDENSENTRATING("condensentrator", "decondensentrating"),
    //Upgrades
    UPGRADE_DISPLAY("upgrade", "display"),
    UPGRADE_DISPLAY_LEVEL("upgrade", "display.level"),
    UPGRADES_EFFECT("gui", "upgrades.effect"),
    UPGRADES("gui", "upgrades"),
    UPGRADE_NO_SELECTION("gui", "upgrades.no_selection"),
    UPGRADES_SUPPORTED("gui", "upgrades.supported"),
    UPGRADE_COUNT("gui", "upgrades.amount"),
    UPGRADE_TYPE("gui", "upgrade"),
    //Filter
    CREATE_FILTER_TITLE("filter", "select.title"),
    FILTERS("filter", "filters"),
    FILTER_COUNT("filter", "filter.count"),
    FILTER_ALLOW_DEFAULT("filter", "allow_default"),
    FILTER("filter", "filter"),
    FILTER_NEW("filter", "new"),
    FILTER_EDIT("filter", "edit"),
    SIZE_MODE("filter", "size_mode"),
    SIZE_MODE_CONFLICT("filter", "size_mode.conflict"),
    MATERIAL_FILTER("filter", "material"),
    MATERIAL_FILTER_DETAILS("filter", "material.details"),
    TAG_FILTER("filter", "tag"),
    TAG_FILTER_NO_TAG("filter", "tag.no_key"),
    TAG_FILTER_SAME_TAG("filter", "tag.same_key"),
    TAG_FILTER_TAG("filter", "tag.key"),
    MODID_FILTER("filter", "modid"),
    MODID_FILTER_NO_ID("filter", "modid.no_id"),
    MODID_FILTER_SAME_ID("filter", "modid.same_id"),
    MODID_FILTER_ID("filter", "modid.id"),
    ITEM_FILTER("filter", "item"),
    ITEM_FILTER_NO_ITEM("filter", "item.no_item"),
    ITEM_FILTER_SIZE_MODE("filter", "item.size_mode"),
    FUZZY_MODE("filter", "fuzzy_mode"),
    ITEM_FILTER_MAX_LESS_THAN_MIN("filter", "item.max_less_than_min"),
    ITEM_FILTER_OVER_SIZED("filter", "item.over_sized"),
    ITEM_FILTER_SIZE_MISSING("filter", "item.size_missing"),
    OREDICTIONIFICATOR_FILTER("filter", "oredictionificator"),
    OREDICTIONIFICATOR_FILTER_INCOMPATIBLE_TAG("filter", "oredictionificator.invalid_key"),
    //Seismic Vibrator
    CHUNK("seismic_vibrator", "chunk"),
    VIBRATING("seismic_vibrator", "vibrating"),
    //Seismic Reader
    NEEDS_ENERGY("seismic_reader", "needs_energy"),
    NO_VIBRATIONS("seismic_reader", "no_vibrations"),
    ABUNDANCY("seismic_reader", "abundancy"),
    //Redstone Control
    REDSTONE_CONTROL_DISABLED("redstone_control", "disabled"),
    REDSTONE_CONTROL_HIGH("redstone_control", "high"),
    REDSTONE_CONTROL_LOW("redstone_control", "low"),
    REDSTONE_CONTROL_PULSE("redstone_control", "pulse"),
    //Security
    SECURITY("tooltip", "security"),
    SECURITY_OVERRIDDEN("security", "overridden"),
    SECURITY_OFFLINE("security", "offline"),
    SECURITY_ADD("security", "add"),
    SECURITY_OVERRIDE("security", "override"),
    NO_ACCESS("security", "no_access"),
    TRUSTED_PLAYERS("security", "trusted_players"),
    PUBLIC("security", "public"),
    TRUSTED("security", "trusted"),
    PRIVATE("security", "private"),
    PUBLIC_MODE("security", "public.mode"),
    TRUSTED_MODE("security", "trusted.mode"),
    PRIVATE_MODE("security", "private.mode"),
    //Formulaic Assemblicator
    ENCODE_FORMULA("assemblicator", "encode_formula"),
    CRAFT_SINGLE("assemblicator", "craft_single"),
    CRAFT_AVAILABLE("assemblicator", "craft_available"),
    FILL_EMPTY("assemblicator", "fill_empty"),
    STOCK_CONTROL("assemblicator", "stock_control"),
    AUTO_MODE("assemblicator", "auto_mode_toggle"),
    //Factory Type
    FACTORY_TYPE("factory", "type"),
    //Transmitter Networks
    NETWORK_DESCRIPTION("network", "description"),
    INVENTORY_NETWORK("network", "inventory"),
    FLUID_NETWORK("network", "fluid"),
    CHEMICAL_NETWORK("network", "chemical"),
    HEAT_NETWORK("network", "heat"),
    ENERGY_NETWORK("network", "energy"),
    NO_NETWORK("network", "no_network"),
    HEAT_NETWORK_STORED("network", "heat_stored"),
    HEAT_NETWORK_FLOW("network", "heat_flow"),
    HEAT_NETWORK_FLOW_EFFICIENCY("network", "heat_flow.efficiency"),
    FLUID_NETWORK_NEEDED("network", "fluid_needed"),
    NETWORK_MB_PER_TICK("network", "mb_per_tick"),
    NETWORK_MB_STORED("network", "mb_stored"),
    //Button
    BUTTON_CONFIRM("button", "confirm"),
    BUTTON_START("button", "start"),
    BUTTON_STOP("button", "stop"),
    BUTTON_CONFIG("button", "config"),
    BUTTON_REMOVE("button", "remove"),
    BUTTON_CANCEL("button", "cancel"),
    BUTTON_SAVE("button", "save"),
    BUTTON_SET("button", "set"),
    BUTTON_DELETE("button", "delete"),
    BUTTON_OPTIONS("button", "options"),
    BUTTON_TELEPORT("button", "teleport"),
    BUTTON_NEW_FILTER("button", "new_filter"),
    BUTTON_ITEMSTACK_FILTER("button", "itemstack_filter"),
    BUTTON_TAG_FILTER("button", "tag_filter"),
    BUTTON_MATERIAL_FILTER("button", "material_filter"),
    BUTTON_MODID_FILTER("button", "modid_filter"),
    //Configuration Card
    CONFIG_CARD_GOT("configuration_card", "got"),
    CONFIG_CARD_SET("configuration_card", "set"),
    CONFIG_CARD_UNEQUAL("configuration_card", "unequal"),
    CONFIG_CARD_HAS_DATA("configuration_card", "has_data"),
    //Connection Type
    CONNECTION_NORMAL("connection", "normal"),
    CONNECTION_PUSH("connection", "push"),
    CONNECTION_PULL("connection", "pull"),
    CONNECTION_NONE("connection", "none"),
    //Teleporter
    TELEPORTER_READY("teleporter", "ready"),
    TELEPORTER_NO_FRAME("teleporter", "no_frame"),
    TELEPORTER_NO_LINK("teleporter", "no_link"),
    TELEPORTER_NEEDS_ENERGY("teleporter", "needs_energy"),
    //Matrix
    MATRIX("matrix", "induction_matrix"),
    MATRIX_RECEIVING_RATE("matrix", "receiving_rate"),
    MATRIX_OUTPUT_AMOUNT("matrix", "output_amount"),
    MATRIX_OUTPUT_RATE("matrix", "output_rate"),
    MATRIX_OUTPUTTING_RATE("matrix", "outputting_rate"),
    MATRIX_INPUT_AMOUNT("matrix", "input_amount"),
    MATRIX_INPUT_RATE("matrix", "input_rate"),
    MATRIX_CONSTITUENTS("matrix", "constituents"),
    MATRIX_DIMENSIONS("matrix", "dimensions"),
    MATRIX_DIMENSION_REPRESENTATION("matrix", "dimensions.representation"),
    MATRIX_STATS("matrix", "stats"),
    MATRIX_CELLS("matrix", "cells"),
    MATRIX_PROVIDERS("matrix", "providers"),
    INDUCTION_PORT_MODE("matrix", "induction_port.configurator_mode"),
    INDUCTION_PORT_OUTPUT_RATE("matrix", "induction_port.output_rate"),
    //Miner
    MINER_INSUFFICIENT_BUFFER("miner", "insufficient_buffer"),
    MINER_BUFFER_FREE("miner", "buffer_free"),
    MINER_TO_MINE("miner", "to_mine"),
    MINER_SILK_ENABLED("miner", "silk_enabled"),
    MINER_AUTO_PULL("miner", "auto_pull"),
    MINER_RUNNING("miner", "running"),
    MINER_LOW_POWER("miner", "low_power"),
    MINER_ENERGY_CAPACITY("miner", "energy_capacity"),
    MINER_MISSING_BLOCK("miner", "missing_block"),
    MINER_WELL("miner", "well"),
    MINER_CONFIG("miner", "config"),
    MINER_SILK("miner", "silk_touch"),
    MINER_RESET("miner", "reset"),
    MINER_INVERSE("miner", "inverse"),
    MINER_VISUALS("miner", "visuals"),
    MINER_VISUALS_TOO_BIG("miner", "visuals.too_big"),
    MINER_REQUIRE_REPLACE("miner", "require_replace"),
    MINER_IS_INVERSE("miner", "is_inverse"),
    MINER_RADIUS("miner", "radius"),
    MINER_IDLE("miner", "idle"),
    MINER_SEARCHING("miner", "searching"),
    MINER_PAUSED("miner", "paused"),
    MINER_READY("miner", "ready"),
    //Boiler
    BOILER("boiler", "thermoelectric_boiler"),
    BOILER_STATS("boiler", "stats"),
    BOILER_MAX_WATER("boiler", "max_water"),
    BOILER_MAX_STEAM("boiler", "max_steam"),
    BOILER_HEAT_TRANSFER("boiler", "heat_transfer"),
    BOILER_HEATERS("boiler", "heaters"),
    BOILER_CAPACITY("boiler", "capacity"),
    BOIL_RATE("boiler", "boil_rate"),
    MAX_BOIL_RATE("boiler", "max_boil"),
    BOILER_VALVE_MODE_CHANGE("boiler", "valve_mode_change"),
    BOILER_VALVE_MODE_INPUT("boiler", "valve_mode_input"),
    BOILER_VALVE_MODE_OUTPUT_STEAM("boiler", "valve_mode_output_steam"),
    BOILER_VALVE_MODE_OUTPUT_COOLANT("boiler", "valve_mode_output_coolant"),
    BOILER_WATER_TANK("boiler", "water_tank"),
    BOILER_COOLANT_TANK("boiler", "coolant_tank"),
    BOILER_STEAM_TANK("boiler", "steam_tank"),
    BOILER_HEATED_COOLANT_TANK("fission", "heated_coolant_tank"),
    //Temperature
    TEMPERATURE("temperature", "short"),
    TEMPERATURE_LONG("temperature", "long"),
    TEMPERATURE_KELVIN("temperature", "kelvin"),
    TEMPERATURE_KELVIN_SHORT("temperature", "kelvin.short"),
    TEMPERATURE_CELSIUS("temperature", "celsius"),
    TEMPERATURE_CELSIUS_SHORT("temperature", "celsius.short"),
    TEMPERATURE_RANKINE("temperature", "rankine"),
    TEMPERATURE_RANKINE_SHORT("temperature", "rankine.short"),
    TEMPERATURE_FAHRENHEIT("temperature", "fahrenheit"),
    TEMPERATURE_FAHRENHEIT_SHORT("temperature", "fahrenheit.short"),
    TEMPERATURE_AMBIENT("temperature", "ambient"),
    TEMPERATURE_AMBIENT_SHORT("temperature", "ambient.short"),
    //Energy
    ENERGY_JOULES("energy", "joules"),
    ENERGY_JOULES_PLURAL("energy", "joules.plural"),
    ENERGY_JOULES_SHORT("energy", "joules.short"),
    ENERGY_FORGE("energy", "forge"),
    ENERGY_FORGE_SHORT("energy", "forge.short"),
    ENERGY_EU("energy", "eu"),
    ENERGY_EU_PLURAL("energy", "eu.plural"),
    ENERGY_EU_SHORT("energy", "eu.short"),
    //Network Reader
    NETWORK_READER_BORDER("network_reader", "border"),
    NETWORK_READER_TEMPERATURE("network_reader", "temperature"),
    NETWORK_READER_TRANSMITTERS("network_reader", "transmitters"),
    NETWORK_READER_ACCEPTORS("network_reader", "acceptors"),
    NETWORK_READER_NEEDED("network_reader", "needed"),
    NETWORK_READER_BUFFER("network_reader", "buffer"),
    NETWORK_READER_THROUGHPUT("network_reader", "throughput"),
    NETWORK_READER_CAPACITY("network_reader", "capacity"),
    NETWORK_READER_CONNECTED_SIDES("network_reader", "connected"),
    //Sorter
    SORTER_DEFAULT("logistical_sorter", "default"),
    SORTER_SINGLE_ITEM("logistical_sorter", "single_item"),
    SORTER_ROUND_ROBIN("logistical_sorter", "round_robin"),
    SORTER_AUTO_EJECT("logistical_sorter", "auto_eject"),
    SORTER_SINGLE_ITEM_DESCRIPTION("logistical_sorter", "single_item.description"),
    SORTER_ROUND_ROBIN_DESCRIPTION("logistical_sorter", "round_robin.description"),
    SORTER_AUTO_EJECT_DESCRIPTION("logistical_sorter", "auto_eject.description"),
    //Side data/config
    SIDE_DATA_NONE("side_data", "none"),
    SIDE_DATA_INPUT("side_data", "input"),
    SIDE_DATA_INPUT_1("side_data", "input_1"),
    SIDE_DATA_INPUT_2("side_data", "input_2"),
    SIDE_DATA_OUTPUT("side_data", "output"),
    SIDE_DATA_OUTPUT_1("side_data", "output_1"),
    SIDE_DATA_OUTPUT_2("side_data", "output_2"),
    SIDE_DATA_INPUT_OUTPUT("side_data", "input_output"),
    SIDE_DATA_ENERGY("side_data", "energy"),
    SIDE_DATA_EXTRA("side_data", "extra"),
    //Free runner modes
    FREE_RUNNER_MODE_CHANGE("free_runner", "mode_change"),
    FREE_RUNNER_NORMAL("free_runner", "normal"),
    FREE_RUNNER_DISABLED("free_runner", "disabled"),
    //Jetpack Modes
    JETPACK_MODE_CHANGE("jetpack", "mode_change"),
    JETPACK_NORMAL("jetpack", "normal"),
    JETPACK_HOVER("jetpack", "hover"),
    JETPACK_DISABLED("jetpack", "disabled"),
    //Disassembler Mode
    DISASSEMBLER_MODE_CHANGE("disassembler", "mode_change"),
    DISASSEMBLER_EFFICIENCY("disassembler", "efficiency"),
    DISASSEMBLER_NORMAL("disassembler", "normal"),
    DISASSEMBLER_SLOW("disassembler", "slow"),
    DISASSEMBLER_FAST("disassembler", "fast"),
    DISASSEMBLER_VEIN("disassembler", "vein"),
    DISASSEMBLER_EXTENDED_VEIN("disassembler", "extended_vein"),
    DISASSEMBLER_OFF("disassembler", "off"),
    //Flamethrower Modes
    FLAMETHROWER_MODE_CHANGE("flamethrower", "mode_change"),
    FLAMETHROWER_COMBAT("flamethrower", "combat"),
    FLAMETHROWER_HEAT("flamethrower", "heat"),
    FLAMETHROWER_INFERNO("flamethrower", "inferno"),
    //Configurator
    CONFIGURE_STATE("configurator", "configure_state"),
    STATE("configurator", "state"),
    TOGGLE_COLOR("configurator", "toggle_color"),
    CURRENT_COLOR("configurator", "view_color"),
    PUMP_RESET("configurator", "pump_reset"),
    PLENISHER_RESET("configurator", "plenisher_reset"),
    REDSTONE_SENSITIVITY("configurator", "redstone_sensitivity"),
    CONNECTION_TYPE("configurator", "mode_change"),
    //Configurator Modes
    CONFIGURATOR_VIEW_MODE("configurator", "view_mode"),
    CONFIGURATOR_TOGGLE_MODE("configurator", "toggle_mode"),
    CONFIGURATOR_CONFIGURATE("configurator", "configurate"),
    CONFIGURATOR_EMPTY("configurator", "empty"),
    CONFIGURATOR_ROTATE("configurator", "rotate"),
    CONFIGURATOR_WRENCH("configurator", "wrench"),
    //Robit
    ROBIT("robit", "robit"),
    ROBIT_NAME("robit", "name"),
    ROBIT_SMELTING("robit", "smelting"),
    ROBIT_CRAFTING("robit", "crafting"),
    ROBIT_INVENTORY("robit", "inventory"),
    ROBIT_REPAIR("robit", "repair"),
    ROBIT_TELEPORT("robit", "teleport"),
    ROBIT_TOGGLE_PICKUP("robit", "toggle_pickup"),
    ROBIT_RENAME("robit", "rename"),
    ROBIT_TOGGLE_FOLLOW("robit", "toggle_follow"),
    ROBIT_GREETING("robit", "greeting"),
    ROBIT_OWNER("robit", "owner"),
    ROBIT_FOLLOWING("robit", "following"),
    ROBIT_DROP_PICKUP("robit", "drop_pickup"),
    //Descriptions
    DESCRIPTION_DICTIONARY("description", "dictionary"),
    DESCRIPTION_SEISMIC_READER("description", "seismic_reader"),
    DESCRIPTION_BIN("description", "bin"),
    DESCRIPTION_TELEPORTER_FRAME("description", "teleporter_frame"),
    DESCRIPTION_STEEL_CASING("description", "steel_casing"),
    DESCRIPTION_DYNAMIC_TANK("description", "dynamic_tank"),
    DESCRIPTION_STRUCTURAL_GLASS("description", "structural_glass"),
    DESCRIPTION_DYNAMIC_VALVE("description", "dynamic_valve"),
    DESCRIPTION_THERMAL_EVAPORATION_CONTROLLER("description", "thermal_evaporation_controller"),
    DESCRIPTION_THERMAL_EVAPORATION_VALVE("description", "thermal_evaporation_valve"),
    DESCRIPTION_THERMAL_EVAPORATION_BLOCK("description", "thermal_evaporation_block"),
    DESCRIPTION_INDUCTION_CASING("description", "induction_casing"),
    DESCRIPTION_INDUCTION_PORT("description", "induction_port"),
    DESCRIPTION_INDUCTION_CELL("description", "induction_cell"),
    DESCRIPTION_INDUCTION_PROVIDER("description", "induction_provider"),
    DESCRIPTION_SUPERHEATING_ELEMENT("description", "superheating_element"),
    DESCRIPTION_PRESSURE_DISPERSER("description", "pressure_disperser"),
    DESCRIPTION_BOILER_CASING("description", "boiler_casing"),
    DESCRIPTION_BOILER_VALVE("description", "boiler_valve"),
    DESCRIPTION_SECURITY_DESK("description", "security_desk"),
    DESCRIPTION_ENRICHMENT_CHAMBER("description", "enrichment_chamber"),
    DESCRIPTION_OSMIUM_COMPRESSOR("description", "osmium_compressor"),
    DESCRIPTION_COMBINER("description", "combiner"),
    DESCRIPTION_CRUSHER("description", "crusher"),
    DESCRIPTION_DIGITAL_MINER("description", "digital_miner"),
    DESCRIPTION_METALLURGIC_INFUSER("description", "metallurgic_infuser"),
    DESCRIPTION_PURIFICATION_CHAMBER("description", "purification_chamber"),
    DESCRIPTION_ENERGIZED_SMELTER("description", "energized_smelter"),
    DESCRIPTION_TELEPORTER("description", "teleporter"),
    DESCRIPTION_ELECTRIC_PUMP("description", "electric_pump"),
    DESCRIPTION_PERSONAL_CHEST("description", "personal_chest"),
    DESCRIPTION_CHARGEPAD("description", "chargepad"),
    DESCRIPTION_LOGISTICAL_SORTER("description", "logistical_sorter"),
    DESCRIPTION_ROTARY_CONDENSENTRATOR("description", "rotary_condensentrator"),
    DESCRIPTION_CHEMICAL_INJECTION_CHAMBER("description", "chemical_injection_chamber"),
    DESCRIPTION_ELECTROLYTIC_SEPARATOR("description", "electrolytic_separator"),
    DESCRIPTION_PRECISION_SAWMILL("description", "precision_sawmill"),
    DESCRIPTION_CHEMICAL_DISSOLUTION_CHAMBER("description", "chemical_dissolution_chamber"),
    DESCRIPTION_CHEMICAL_WASHER("description", "chemical_washer"),
    DESCRIPTION_CHEMICAL_CRYSTALLIZER("description", "chemical_crystallizer"),
    DESCRIPTION_CHEMICAL_OXIDIZER("description", "chemical_oxidizer"),
    DESCRIPTION_CHEMICAL_INFUSER("description", "chemical_infuser"),
    DESCRIPTION_SEISMIC_VIBRATOR("description", "seismic_vibrator"),
    DESCRIPTION_PRESSURIZED_REACTION_CHAMBER("description", "pressurized_reaction_chamber"),
    DESCRIPTION_FLUID_TANK("description", "fluid_tank"),
    DESCRIPTION_FLUIDIC_PLENISHER("description", "fluidic_plenisher"),
    DESCRIPTION_LASER("description", "laser"),
    DESCRIPTION_LASER_AMPLIFIER("description", "laser_amplifier"),
    DESCRIPTION_LASER_TRACTOR_BEAM("description", "laser_tractor_beam"),
    DESCRIPTION_SOLAR_NEUTRON_ACTIVATOR("description", "solar_neutron_activator"),
    DESCRIPTION_OREDICTIONIFICATOR("description", "oredictionificator"),
    DESCRIPTION_FACTORY("description", "factory"),
    DESCRIPTION_RESISTIVE_HEATER("description", "resistive_heater"),
    DESCRIPTION_FORMULAIC_ASSEMBLICATOR("description", "formulaic_assemblicator"),
    DESCRIPTION_FUELWOOD_HEATER("description", "fuelwood_heater"),
    DESCRIPTION_MODIFICATION_STATION("description", "modification_station"),
    DESCRIPTION_ISOTOPIC_CENTRIFUGE("description", "isotopic_centrifuge"),
    DESCRIPTION_QUANTUM_ENTANGLOPORTER("description", "quantum_entangloporter"),
    DESCRIPTION_NUTRITIONAL_LIQUIFIER("description", "nutritional_liquifier"),
    DESCRIPTION_ANTIPROTONIC_NUCLEOSYNTHESIZER("description", "antiprotonic_nucleosynthesizer"),
    DESCRIPTION_QIO_DRIVE_ARRAY("description", "qio_drive_array"),
    DESCRIPTION_QIO_DASHBOARD("description", "qio_dashboard"),
    DESCRIPTION_QIO_IMPORTER("description", "qio_importer"),
    DESCRIPTION_QIO_EXPORTER("description", "qio_exporter"),
    DESCRIPTION_QIO_REDSTONE_ADAPTER("description", "qio_redstone_adapter"),
    DESCRIPTION_RADIOACTIVE_WASTE_BARREL("description", "radioactive_waste_barrel"),
    DESCRIPTION_INDUSTRIAL_ALARM("description", "industrial_alarm"),
    DESCRIPTION_ENERGY_CUBE("description", "energy_cube"),
    DESCRIPTION_CHEMICAL_TANK("description", "chemical_tank"),
    DESCRIPTION_DIVERSION("description", "diversion"),
    DESCRIPTION_RESTRICTIVE("description", "restrictive"),
    DESCRIPTION_SPS_CASING("description", "sps_casing"),
    DESCRIPTION_SPS_PORT("description", "sps_port"),
    DESCRIPTION_SUPERCHARGED_COIL("description", "supercharged_coil"),
    //Factory Type
    SMELTING("factory", "smelting"),
    ENRICHING("factory", "enriching"),
    CRUSHING("factory", "crushing"),
    COMPRESSING("factory", "compressing"),
    COMBINING("factory", "combining"),
    PURIFYING("factory", "purifying"),
    INJECTING("factory", "injecting"),
    INFUSING("factory", "infusing"),
    SAWING("factory", "sawing"),
    //Modules
    MODULE_ENABLED("module", "enabled"),
    MODULE_ENABLED_LOWER("module", "enabled_lower"),
    MODULE_DISABLED_LOWER("module", "disabled_lower"),
    MODULE_DAMAGE("module", "damage"),
    MODULE_TWEAKER("module", "module_tweaker"),
    MODULE_INSTALLED("module", "installed"),
    MODULE_STACKABLE("module", "stackable"),
    MODULE_EXCLUSIVE("module", "exclusive"),
    MODULE_HANDLE_MODE_CHANGE("module", "handle_mode_change"),
    MODULE_RENDER_HUD("module", "render_hud"),
    MODULE_MODE("module", "mode"),
    MODULE_ATTACK_DAMAGE("module", "attack_damage"),
    MODULE_FARMING_RADIUS("module", "farming_radius"),
    MODULE_JUMP_BOOST("module", "jump_boost"),
    MODULE_STEP_ASSIST("module", "step_assist"),
    MODULE_RANGE("module", "range"),
    MODULE_SPRINT_BOOST("module", "sprint_boost"),
    MODULE_EXTENDED_MODE("module", "extended_mode"),
    MODULE_EXTENDED_ENABLED("module", "extended_enabled"),
    MODULE_EXCAVATION_RANGE("module", "mining_range"),
    MODULE_EFFICIENCY("module", "efficiency"),
    MODULE_JETPACK_MODE("module", "jetpack_mode"),
    MODULE_GRAVITATIONAL_MODULATION("module", "gravitational_modulation"),
    MODULE_MODE_CHANGE("module", "mode_change"),
    MODULE_CHARGE_SUIT("module", "charge_suit"),
    MODULE_CHARGE_INVENTORY("module", "charge_inventory"),
    MODULE_SPEED_BOOST("module", "speed_boost"),
    MODULE_VISION_ENHANCEMENT("module", "vision_enhancement"),
    MODULE_PURIFICATION_BENEFICIAL("module", "purification.beneficial"),
    MODULE_PURIFICATION_NEUTRAL("module", "purification.neutral"),
    MODULE_PURIFICATION_HARMFUL("module", "purification.harmful"),

    MODULE_ENERGY_UNIT("module", "energy_unit"),
    MODULE_EXCAVATION_ESCALATION_UNIT("module", "excavation_escalation"),
    MODULE_ATTACK_AMPLIFICATION_UNIT("module", "attack_amplification_unit"),
    MODULE_SILK_TOUCH_UNIT("module", "silk_touch_unit"),
    MODULE_VEIN_MINING_UNIT("module", "vein_mining_unit"),
    MODULE_FARMING_UNIT("module", "farming_unit"),
    MODULE_TELEPORTATION_UNIT("module", "teleportation_unit"),
    MODULE_ELECTROLYTIC_BREATHING_UNIT("module", "electrolytic_breathing_unit"),
    MODULE_INHALATION_PURIFICATION_UNIT("module", "inhalation_purification_unit"),
    MODULE_VISION_ENHANCEMENT_UNIT("module", "vision_enhancement_unit"),
    MODULE_SOLAR_RECHARGING_UNIT("module", "solar_recharging_unit"),
    MODULE_NUTRITIONAL_INJECTION_UNIT("module", "nutritional_injection_unit"),
    MODULE_RADIATION_SHIELDING_UNIT("module", "radiation_shielding_unit"),
    MODULE_JETPACK_UNIT("module", "jetpack_unit"),
    MODULE_GRAVITATIONAL_MODULATING_UNIT("module", "gravitational_modulating_unit"),
    MODULE_CHARGE_DISTRIBUTION_UNIT("module", "charge_distribution_unit"),
    MODULE_DOSIMETER_UNIT("module", "dosimeter_unit"),
    MODULE_LOCOMOTIVE_BOOSTING_UNIT("module", "locomotive_boosting_unit"),
    MODULE_HYDRAULIC_PROPULSION_UNIT("module", "hydraulic_propulsion_unit"),
    MODULE_MAGNETIC_ATTRACTION_UNIT("module", "magnetic_attraction_unit"),

    DESCRIPTION_ENERGY_UNIT("description", "energy_unit"),
    DESCRIPTION_EXCAVATION_ESCALATION_UNIT("description", "excavation_escalation_unit"),
    DESCRIPTION_ATTACK_AMPLIFICATION_UNIT("description", "attack_amplification_unit"),
    DESCRIPTION_SILK_TOUCH_UNIT("description", "silk_touch_unit"),
    DESCRIPTION_VEIN_MINING_UNIT("description", "vein_mining_unit"),
    DESCRIPTION_FARMING_UNIT("description", "farming_unit"),
    DESCRIPTION_TELEPORTATION_UNIT("description", "teleportation_unit"),
    DESCRIPTION_ELECTROLYTIC_BREATHING_UNIT("description", "electrolytic_breathing_unit"),
    DESCRIPTION_INHALATION_PURIFICATION_UNIT("description", "inhalation_purification_unit"),
    DESCRIPTION_VISION_ENHANCEMENT_UNIT("description", "vision_enhancement_unit"),
    DESCRIPTION_SOLAR_RECHARGING_UNIT("description", "solar_recharging_unit"),
    DESCRIPTION_NUTRITIONAL_INJECTION_UNIT("description", "nutritional_injection_unit"),
    DESCRIPTION_RADIATION_SHIELDING_UNIT("description", "radiation_shielding_unit"),
    DESCRIPTION_JETPACK_UNIT("description", "jetpack_unit"),
    DESCRIPTION_GRAVITATIONAL_MODULATING_UNIT("description", "gravitational_modulating_unit"),
    DESCRIPTION_CHARGE_DISTRIBUTION_UNIT("description", "charge_distribution_unit"),
    DESCRIPTION_DOSIMETER_UNIT("description", "dosimeter_unit"),
    DESCRIPTION_LOCOMOTIVE_BOOSTING_UNIT("description", "locomotive_boosting_unit"),
    DESCRIPTION_HYDRAULIC_PROPULSION_UNIT("description", "hydraulic_propulsion_unit"),
    DESCRIPTION_MAGNETIC_ATTRACTION_UNIT("description", "magnetic_attraction_unit");

    private final String key;

    MekanismLang(String type, String path) {
        this(Util.makeTranslationKey(type, Mekanism.rl(path)));
    }

    MekanismLang(String key) {
        this.key = key;
    }

    @Override
    public String getTranslationKey() {
        return key;
    }

    public static MekanismLang get(EquipmentSlotType type) {
        switch (type) {
            case HEAD:
                return HEAD;
            case CHEST:
                return BODY;
            case LEGS:
                return LEGS;
            case FEET:
                return FEET;
            case MAINHAND:
                return MAINHAND;
            case OFFHAND:
                return OFFHAND;
        }
        return null;
    }
}