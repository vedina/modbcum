/* AbstractUpdate.java
 * Author: nina
 * Date: Nov 14, 2014
 * 
 * Copyright (C) 2005-2014  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package net.idea.modbcum.q.update;

import java.util.logging.Logger;

import net.idea.modbcum.i.query.IQueryUpdate;

public abstract class AbstractUpdate<Group, Target> implements IQueryUpdate<Group, Target> {
    protected static Logger logger = Logger.getLogger(AbstractUpdate.class.getName());
    protected Target object;
    protected Group group;

    public Group getGroup() {
	return group;
    }

    public void setGroup(Group group) {
	this.group = group;
    }

    public AbstractUpdate(Target target) {
	setObject(target);
    }

    public AbstractUpdate() {
	this(null);
    }

    public Target getObject() {
	return object;
    }

    public void setObject(Target object) {
	this.object = object;
    }

    public boolean returnKeys(int index) {
	return false;
    }

    @Override
    public boolean isStoredProcedure() {
	return false;
    }

    protected String truncate(String g, int length) {
	if (g != null && g.length() > length)
	    return g.substring(0, length - 1);
	return g;
    }
}
