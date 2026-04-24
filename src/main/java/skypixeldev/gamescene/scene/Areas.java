package skypixeldev.gamescene.scene;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import skypixeldev.gamescene.Utils;
import skypixeldev.gamescene.config.GConfig;
import skypixeldev.gamescene.config.OptionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Areas {
    public interface Area{
        boolean contains(Location loc);
        Object getOption(String key);
        String getAreaKey();
    }

    public static class RadiusArea implements Area{
        private Location center;
        @Getter private double radius;
        private String key;
        private Map<String,Object> options;
        protected RadiusArea(String key, Location center, double radius, Map<String,Object> options){
            this.center = center;
            this.key = key;
            this.radius = radius;
            this.options = options;
        }

        @Override
        public boolean contains(Location loc) {
            return loc.distanceSquared(center) <= radius * radius;
        }

        @Override
        public Object getOption(String key) {
            return options.get(key);
        }

        @Override
        public String getAreaKey() {
            return key;
        }
    }

    public static class CuboidArea implements Area{
        private Location corner1;
        private Location corner2;
        private String key;
        private Map<String,Object> options;
        protected CuboidArea(String key, Location first, Location second, Map<String,Object> options){
            this.corner1 = first;
            this.corner2 = second;
            this.key = key;
            this.options = options;
        }


        private boolean fallsBetween(int one, int two, int num) {
            int min, max;
            if (one < two) {
                min = one;
                max = two;
            } else {
                min = two;
                max = one;
            }

            return num >= min && num <= max;
        }

        public List<Location> getBlocks() {
            Location loc1 = corner1;
            Location loc2 = corner2;
            int lowX = Math.min(loc1.getBlockX(), loc2.getBlockX());
            int lowY = Math.min(loc1.getBlockY(), loc2.getBlockY());
            int lowZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

            ArrayList<Location> locs = new ArrayList<>();

            for (int x = 0; x <= Math.abs(loc1.getBlockX() - loc2.getBlockX()); x++) {
                for (int y = 0; y <= Math.abs(loc1.getBlockY() - loc2.getBlockY()); y++) {

                    for (int z = 0; z <= Math.abs(loc1.getBlockZ() - loc2.getBlockZ()); z++) {

                        locs.add(new Location(loc1.getWorld(), lowX + x, lowY + y, lowZ + z));
                    }
                }
            }


            return locs;
        }

        @Override
        public boolean contains(Location loc) {
            if (!corner1.getWorld().equals(loc.getWorld())) {
                return false;
            }

            return fallsBetween(corner1.getBlockX(), corner2.getBlockX(), loc.getBlockX()) &&
                    fallsBetween(corner1.getBlockY(), corner2.getBlockY(), loc.getBlockY()) &&
                    fallsBetween(corner1.getBlockZ(), corner2.getBlockZ(), loc.getBlockZ());
        }

        @Override
        public Object getOption(String key) {
            return options.get(key);
        }

        @Override
        public String getAreaKey() {
            return key;
        }
    }
    @Getter
    private GameScene scene;
    @Getter private GConfig config;
    public Areas(GameScene scene){
        this.scene = scene;
        this.config = scene.getProvider().getOrCreateConfig("Areas");
        this.config.saveToFile();
    }

    public RadiusArea getRadius(String key){
        ConfigurationSection areaSection = config.getConfiguration().getConfigurationSection(key);
        if(areaSection == null){
            throw new NullPointerException("Cannot locate " + key + " in Areas");
        }
        Location positionA = Utils.stringToLocation(areaSection.getString("Pos-A"));
        Location positionB = Utils.stringToLocation(areaSection.getString("Pos-B"));
        if(!positionB.equals(positionA)){
            // Auto Fix
            areaSection.set("Pos-B", Utils.locationToString(positionA));
            areaSection.set("Options.AreaType", "Radius");
            areaSection.set("Radius", 1.0d);
            config.saveToFile();
        }
        if(areaSection.getConfigurationSection("Options") == null){
            areaSection.createSection("Options", OptionBuilder.of()
                    .key("AreaType").value("Radius")
                    .key("Timestamp").value(Utils.getCurrentFormatTime())
                    .key("Radius").value(1.0d).build());
        }
        Map<String,Object> options = OptionBuilder.of(areaSection.getConfigurationSection("Options")).build();
        double radius = (double) options.getOrDefault("Radius",1.0d);
        return new RadiusArea(key, positionA, radius, options);
    }

    public CuboidArea getCuboid(String key){
        ConfigurationSection areaSection = config.getConfiguration().getConfigurationSection(key);
        if(areaSection == null){
            throw new NullPointerException("Cannot locate " + key + " in Areas");
        }
        Location positionA = Utils.stringToLocation(areaSection.getString("Pos-A"));
        Location positionB = Utils.stringToLocation(areaSection.getString("Pos-B"));
        if(areaSection.getConfigurationSection("Options") == null){
            areaSection.createSection("Options", OptionBuilder.of()
                    .key("AreaType").value("Cuboid")
                    .key("Timestamp").value(Utils.getCurrentFormatTime()).build());
        }
        Map<String,Object> options = OptionBuilder.of(areaSection.getConfigurationSection("Options")).build();
        return new CuboidArea(key, positionA, positionB, options);
    }

    public Area getArea(String key){
        ConfigurationSection areaSection = config.getConfiguration().getConfigurationSection(key);
        if(areaSection == null){
            throw new NullPointerException("Cannot locate " + key + " in Areas");
        }
        if(!areaSection.contains("Options.AreaType")){
            throw new IllegalStateException("Cannot auto locate the area type of " + key);
        }
        String areaType = areaSection.getString("Options.AreaType");
        if(areaType.equals("Radius")){
            return getRadius(key);
        }else if(areaType.equals("Cuboid")){
            return getCuboid(key);
        }else{
            throw new IllegalArgumentException("Unknown type of area: " + areaType);
        }
    }

    public CuboidArea saveCuboid(String key, Location corner1, Location corner2){
        ConfigurationSection areaSection = config.getConfiguration().createSection(key);
        areaSection.set("Pos-A", Utils.locationToString(corner1));
        areaSection.set("Pos-B", Utils.locationToString(corner2));
        areaSection.createSection("Options", OptionBuilder.of()
                .key("AreaType").value("Cuboid")
                .key("Timestamp").value(Utils.getCurrentFormatTime())
                .build());
        return new CuboidArea(key, corner1, corner2, OptionBuilder.of(areaSection.getConfigurationSection("Options")).build());
    }

    public RadiusArea saveRadius(String key, Location center, double radius){
        ConfigurationSection areaSection = config.getConfiguration().createSection(key);
        areaSection.set("Pos-A", Utils.locationToString(center));
        areaSection.set("Pos-B", Utils.locationToString(center));
        areaSection.createSection("Options", OptionBuilder.of()
                .key("AreaType").value("Radius")
                .key("Timestamp").value(Utils.getCurrentFormatTime())
                .key("Radius").value(radius).build());
        return new RadiusArea(key, center, radius, OptionBuilder.of(areaSection.getConfigurationSection("Options")).build());
    }


}
