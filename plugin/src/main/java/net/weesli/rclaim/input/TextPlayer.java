package net.weesli.rclaim.input;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public record TextPlayer(WrappedTask task, TextInputManager.TextInputAction action, Object o) {
    public boolean isCancelled() {
        return task.isCancelled();
    }
    public void cancel() {
        task.cancel();
    }
}
