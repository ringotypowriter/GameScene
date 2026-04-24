package skypixeldev.gamescene.parser;

import org.bukkit.Bukkit;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.GlobalSchedulers;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.parser.exception.ParsingFunctionException;
import skypixeldev.gamescene.parser.exception.ParsingWildcardException;
import skypixeldev.gamescene.interfaces.IFunction;
import skypixeldev.gamescene.interfaces.IWildcard;
import skypixeldev.gamescene.parser.wildcard.*;
import skypixeldev.gamescene.scene.GameScene;
import skypixeldev.gamescene.scene.Schedulers;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class FunctionManager {
    private static ConcurrentHashMap<String, IFunction> functions = new ConcurrentHashMap<>();

    private final static WildcardFormatCode formatCode = new WildcardFormatCode();
    private final static WildcardRandom random = new WildcardRandom();
    private final static IWildcard[] TOP_LAYER = {formatCode, random};

    private static boolean hasPAPISupport = false;

    static {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            hasPAPISupport = true;
            Log.lPf("PlaceholderAPI Support Enabled.");
        }
    }

    public static int getLoadedFunctionSize(){
        return functions.size();
    }

    public static void register(IFunction fn) {
        functions.put(fn.getFunction(), fn);
        Log.lPf("Imported Function " + fn.getFunction() + " from " + fn.getProvider());
    }

    public static void execute(List<Parsing> parsings, GameScene whereInvoke, IWildcard... wildcards) {
        boolean delayedRunning = false;
        int ticks = 0;
        Collection<Parsing> remains = new ArrayList<>();
        for (Parsing parsing : parsings) {

            if (!delayedRunning) {
                if (parsing.getFunctionName().equals("wait")) {
                    ticks = Integer.parseInt(parsing.getArguments().getOrDefault("val", "1")) * 20;
                    delayedRunning = true;
                    continue;
                } else if (parsing.getFunctionName().equals("waitTick")) {
                    ticks = Integer.parseInt(parsing.getArguments().getOrDefault("val", "20"));
                    delayedRunning = true;
                    continue;
                }
            }
            if (delayedRunning) {
                remains.add(parsing);
            } else {
                try {
                    execute(parsing, whereInvoke, wildcards);
                }catch (Throwable throwable){
                    Bukkit.getLogger().log(Level.WARNING,"executing exception", throwable);
                }
            }
        }

        if(delayedRunning){
            internalExecuteFunctions(remains, ticks, whereInvoke, wildcards);
        }

    }

    private static void internalExecuteFunctions(final Collection<Parsing> remains,  int ticks, GameScene scene, IWildcard... wildcards) {
        Runnable runnable = () -> {
            boolean delayedRunning = false;
            int secs1 = 0;
            Collection<Parsing> remains1 = new ArrayList<>();
            for (Parsing parsing : remains) {
                // Global Internal Implement
                if (!delayedRunning) {
                    if (parsing.getFunctionName().equals("wait")) {
                        secs1 = Integer.parseInt(parsing.getArguments().getOrDefault("val", "1")) * 20;
                        delayedRunning = true;
                        continue;
                    } else if (parsing.getFunctionName().equals("waitTick")) {
                        secs1 = Integer.parseInt(parsing.getArguments().getOrDefault("val", "20"));
                        delayedRunning = true;
                        continue;
                    }
                }
                if (delayedRunning) {
                    remains1.add(parsing);
                } else {
                    try {
                        execute(parsing, scene, wildcards);
                    }catch (Throwable throwable){
                        Bukkit.getLogger().log(Level.WARNING,"executing exception", throwable);
                    }
                }
            }
            if (delayedRunning) {
                internalExecuteFunctions(remains1, secs1, scene, wildcards);
            }
        };
        scene.getSchedulers().runSyncLater(runnable, ticks);
    }

    public static void execute(Parsing parsing, GameScene whereInvoke, IWildcard... wildcards){
        execute(parsing.getFunctionName(), parsing.getArguments(), whereInvoke, wildcards);
    }

    public static void execute(String functionName, Map<String,String> arguments, GameScene whereInvoke, IWildcard... wildcards) {
        execute(functionName, arguments,whereInvoke,false, wildcards);
    }

    public static void execute(String functionName, Map<String,String> arguments, GameScene whereInvoke, boolean isAsync, IWildcard... wildcards) {
        if (!functions.containsKey(functionName)) {
            throw new ParsingFunctionException("Invalid function name " + functionName);
        }
        IFunction function = functions.get(functionName);
        Map<String, String> argumentMap = new HashMap<>(arguments);

        WildcardPlayer wcPlayer = null; // PAPI

        for (IWildcard wildcard : wildcards) {
            try {
                argumentMap = wildcard.replaceWildcard(argumentMap);
                if(wildcard instanceof WildcardPlayer) {
                    wcPlayer = (WildcardPlayer) wildcard;
                }
            } catch (Exception exc) {
                exc.printStackTrace();
                throw new ParsingWildcardException(wildcard);
            }

            // Auto fill
            if(wildcard instanceof WildcardPlayer){
                Selector.fill(argumentMap,((WildcardPlayer) wildcard).getForReplace().getName(),"p","player","pl");
            }

            if(whereInvoke != null){
                Selector.fill(argumentMap,whereInvoke.getName(),"gs","gameScene","scene","GameScene");
            }else if(wildcard instanceof WildcardGameScene){
                Selector.fill(argumentMap,((WildcardGameScene) wildcard).getScene().getName(),"gs","gameScene","scene","GameScene");
            }
        }

        // Top Layer
        for(IWildcard wildcard : TOP_LAYER) {
            try {
                argumentMap = wildcard.replaceWildcard(argumentMap);
            } catch (Exception exc) {
                exc.printStackTrace();
                throw new ParsingWildcardException(wildcard);
            }
        }

        // PlaceholderAPI Integration
        if(hasPAPISupport && wcPlayer != null){
            argumentMap = new WildcardPlaceholder(wcPlayer).replaceWildcard(argumentMap);
        }

        if(isAsync){
            Map<String, String> finalArgumentMap = argumentMap;
            GlobalSchedulers.getInstance().runAsync(() -> {
                if (!function.execute(finalArgumentMap, whereInvoke,true)) {
                    throw new ParsingFunctionException("function " + function.getFunction() + " doesn't run correctly");
                }
            });
        }else {
            if (!function.execute(argumentMap, whereInvoke,false)) {
                throw new ParsingFunctionException("function " + function.getFunction() + " doesn't run correctly");
            }
        }
    }

}
