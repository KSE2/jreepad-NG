/*
           Jreepad - personal information manager.
           Copyright (C) 2004-2006 Dan Stowell

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

The full license can be read online here:

           http://www.gnu.org/copyleft/gpl.html
*/

package jreepad.io;

import java.io.IOException;
import java.io.OutputStream;

import jreepad.JreepadTreeModel;

/**
 * Interface for classes that export Jreepad trees.
 *
 * @author <a href="mailto:pewu@losthive.org">Przemek Więch</a>
 * @version $Id$
 */
public interface JreepadWriter
{
    public void write(OutputStream out, JreepadTreeModel document) throws IOException;
}
