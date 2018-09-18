/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.capabilities;

public class Undying implements IUndying {

    private static final int SMITE_DURATION = 200;

    private int smite = 0;

    @Override
    public boolean isSmote() {
        return smite > 0;
    }

    @Override
    public int getSmite() {
        return smite;
    }

    @Override
    public void setSmite(int duration) {
        smite = duration;
    }

    @Override
    public void decrementSmite() {

        if (smite > 0) {
            smite--;
        }
    }
}
