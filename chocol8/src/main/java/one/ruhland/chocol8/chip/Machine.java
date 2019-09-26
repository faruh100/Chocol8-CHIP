package one.ruhland.chocol8.chip;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import one.ruhland.chocol8.generated.BuildConfig;

public class Machine {

    private final Memory memory;
    private final Graphics graphics;
    private final Sound sound;
    private final Keyboard keyboard;
    private final Cpu cpu;
    private final Timer timer;

    private boolean isRunning = false;

    public Machine(final Class<? extends Graphics> graphicsClass, final Class<? extends Sound> soundClass,
                   final Class<? extends Keyboard> keyboardClass) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        memory = new Memory();
        graphics = graphicsClass.getConstructor(int.class, int.class, Memory.class).newInstance(64, 32, memory);
        sound = soundClass.getConstructor().newInstance();
        keyboard = keyboardClass.getConstructor().newInstance();
        timer = new Timer();
        cpu = new Cpu(memory, graphics, sound, keyboard, timer);
    }

    public void reset() {
        stop();
        cpu.reset();
        memory.reset();
        graphics.reset();
        sound.reset();
    }

    public void loadProgram(final String fileName) throws IOException {
        reset();
        memory.setBytes(Memory.PROGRAM_START, Files.readAllBytes(Paths.get(fileName)));
    }

    public void start() {
        cpu.start();
        isRunning = true;
    }

    public void stop() {
        cpu.stop();
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public Memory getMemory() {
        return memory;
    }

    public Graphics getGraphics() {
        return graphics;
    }

    public Sound getSound() {
        return sound;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public Timer getTimer() {
        return timer;
    }

    public Cpu getCpu() {
        return cpu;
    }

    public static void printBanner() {
        InputStream inputStream = Machine.class.getClassLoader().getResourceAsStream("banner.txt");

        if (inputStream == null) {
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String banner = reader.lines().collect(Collectors.joining(System.lineSeparator()));

        System.out.print("\n");
        System.out.printf(banner, BuildConfig.VERSION, BuildConfig.BUILD_DATE, BuildConfig.GIT_BRANCH, BuildConfig.GIT_COMMIT);
        System.out.print("\n\n");
    }
}
