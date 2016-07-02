/*
 * This file is part of BlockJamCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016, BlockJam <https://blockjam.org/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.blockjam.core.util;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.util.function.Consumer;


/**
 * Counts down on a broadcast channel until
 * runs out and calls function given.
 */
public class CountdownTask implements Consumer<Task> {

    public interface MessageFunction{
        public Text f(int timeLeft);
    }

    private int timeLeft;
    private CountdownTask.MessageFunction messageFunc;
    private Runnable func;

    CountdownTask(int timeToCountdown, CountdownTask.MessageFunction message, Runnable endFunction){
        timeLeft = timeToCountdown;
        messageFunc = message;
        func = endFunction;
    }

    @Override
    public void accept(Task task) {
        --timeLeft;
        Sponge.getServer().getBroadcastChannel().send(messageFunc.f(timeLeft));
        if(timeLeft <= 0){
            func.run();
            task.cancel();
        }
    }
}