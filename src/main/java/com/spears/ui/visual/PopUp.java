/*
 * SPEARS: Simulated Physics and Environment for Autonomous Risk Studies
 * Copyright (C) 2017  Colorado School of Mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spears.ui.visual;

public interface PopUp {

    enum Options { OK_OPTION, CANCEL_OPTION, YES_OPTION, NO_OPTION };
    enum Buttons { DEFAULT_OPTIONS, OK_CANCEL_OPTIONS, YES_NO_OPTIONS, YES_NO_CANCEL_OPTIONS };

    PopUp setSubject(String title);

    PopUp setMessage(String text);

    Options showConfirmDialog(Buttons btns);

    int showOptionDialog(String[] options);

    String showInputDialog();

}
