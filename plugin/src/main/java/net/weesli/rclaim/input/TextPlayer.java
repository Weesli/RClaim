package net.weesli.rclaim.input;

import com.tcoded.folialib.wrapper.task.WrappedTask;

public record TextPlayer(WrappedTask task, TextInputManager.TextInputAction action, Object o) {
    public boolean isCancelled() {
        return task.isCancelled();
    }
    public void cancel() {
        task.cancel();
    }
}
