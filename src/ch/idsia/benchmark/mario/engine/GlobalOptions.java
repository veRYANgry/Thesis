/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ch.idsia.benchmark.mario.engine;

import ch.idsia.tools.GameViewer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class GlobalOptions
{
public  final int primaryVerionUID = 0;
public  final int minorVerionUID = 1;
public  final int minorSubVerionID = 9;

public  boolean areLabels = false;
public  boolean isCameraCenteredOnMario = false;
public  Integer FPS = 24;
public  int MaxFPS = 100;
public  boolean areFrozenCreatures = false;

public  boolean isVisualization = true;
public  boolean isGameplayStopped = false;
public  boolean isFly = false;

private  GameViewer GameViewer = null;
//    public static boolean isTimer = true;

public  int mariosecondMultiplier = 15;

public  boolean isPowerRestoration;

// required for rendering grid in ch/idsia/benchmark/mario/engine/sprites/Sprite.java
public  int receptiveFieldWidth = 19;
public  int receptiveFieldHeight = 19;
public  int marioEgoCol = 9;
public  int marioEgoRow = 9;

private  MarioVisualComponent marioVisualComponent;
public  int VISUAL_COMPONENT_WIDTH = 320;
public  int VISUAL_COMPONENT_HEIGHT = 240;

public  boolean isShowReceptiveField = false;
public  boolean isScale2x = false;
public  boolean isRecording = false;
public  boolean isReplaying = false;

public  int getPrimaryVersionUID()
{
    return primaryVerionUID;
}

public  int getMinorVersionUID()
{
    return minorVerionUID;
}

public  int getMinorSubVersionID()
{
    return minorSubVerionID;
}

public  String getBenchmarkName()
{
    return "[~ Mario AI Benchmark ~" + this.getVersionUID() + "]";
}

public  String getVersionUID()
{
    return " " + getPrimaryVersionUID() + "." + getMinorVersionUID() + "." + getMinorSubVersionID();
}

public  void registerMarioVisualComponent(MarioVisualComponent mc)
{
    marioVisualComponent = mc;
}

public  void registerGameViewer(GameViewer gv)
{
    GameViewer = gv;
}

public  void AdjustMarioVisualComponentFPS()
{
    if (marioVisualComponent != null)
        marioVisualComponent.adjustFPS();
}

public  void gameViewerTick()
{
    if (GameViewer != null)
        GameViewer.tick();
}

public  String getDateTime(Long d)
{
    final DateFormat dateFormat = (d == null) ? new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:ms") :
            new SimpleDateFormat("HH:mm:ss:ms");
    if (d != null)
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    final Date date = (d == null) ? new Date() : new Date(d);
    return dateFormat.format(date);
}

final  private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

public  String getTimeStamp()
{
    return dateFormat.format(new Date());
}

public  void changeScale2x()
{
    if (marioVisualComponent == null)
        return;

    isScale2x = !isScale2x;
    marioVisualComponent.width *= isScale2x ? 2 : 0.5;
    marioVisualComponent.height *= isScale2x ? 2 : 0.5;
    marioVisualComponent.changeScale2x();
}
}
