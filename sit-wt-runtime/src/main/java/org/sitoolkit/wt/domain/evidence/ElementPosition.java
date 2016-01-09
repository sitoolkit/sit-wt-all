/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sitoolkit.wt.domain.evidence;

/**
 *
 * @author yuichi.kuwahara
 */
public class ElementPosition {

    public static final ElementPosition EMPTY = new ElementPosition();
    private String no;
    private int x;
    private int y;
    private int w;
    private int h;

    public ElementPosition() {
        super();
    }

    public ElementPosition(String no) {
        this();
        this.no = no;
    }
    
    public ElementPosition(double x, double y, double w, double h) {
        this.x = (int)x;
        this.y = (int)y;
        this.w = (int)w;
        this.h = (int)h;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

}
