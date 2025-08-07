package net.weesli.rclaim.input;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public record TextPlayer(BukkitTask task, TextInputManager.TextInputAction action, Object o) {
    public boolean isCancelled() {
        return task.isCancelled();
    }
    public void cancel() {
        task.cancel();
    }
}
