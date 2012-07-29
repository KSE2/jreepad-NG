/*
 *  Global in jreepad
 *  file: Global.java
 * 
 *  Project jreepad-NG
 *  @author Wolfgang Keller
 *  Created 25.07.2012
 *  Version
 * 
 *  Copyright (c) 2012 by Wolfgang Keller, Munich, Germany
 * 
 This program is not freeware software but copyright protected to the author(s)
 stated above. However, you can use, redistribute and/or modify it under the terms 
 of the GNU General Public License as published by the Free Software Foundation, 
 version 2 of the License.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 Place - Suite 330, Boston, MA 02111-1307, USA, or go to
 http://www.gnu.org/copyleft/gpl.html.
 */

package jreepad;

import java.awt.Color;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.UIManager;

public class Global
{
   // Jreepad version, to appear in "about" box etc
   public static final String PROGRAM_NAME = "JREEPAD-NG";
   public static final String PROGRAM_VERSION = "0.1.0 D1";
   public static final Color STRIP_COLOR = new Color(0xff, 0x8c, 0x00); // netscape.darkorange

   public static ResourceBundle lang;

   private static Locale locale;


   public static void init ( String[] args )
   {
      String hstr;
      
      System.out.println( "# JREEPAD-NG " + PROGRAM_VERSION + " Startup" );
      
      // System ID
      controlJavaVersion();
      
     // System.err.println("" + args.length + " input arguments provided.");
     // for(int i=0; i<args.length; i++){
     //   System.err.println(args[i]);
     // }

      // digest commandline arguments
      ArgumentHandler argHandler = new ArgumentHandler( args );
      hstr = argHandler.getCommandline();
      if ( !hstr.isEmpty() )
         System.out.println( "# Commandline: ".concat( hstr ));
      
      // loading of preferences file
      // TODO
      
      // load resources
      locale = argHandler.getLocale() != null ? argHandler.getLocale() : Locale.getDefault();
      Locale.setDefault( locale );
      System.out.println( "# Locale: ".concat( locale.toString() ));
      lang = ResourceBundle.getBundle("jreepad.lang.JreepadStrings");
      
      // Setup of graphical system (Java side)
      try
      {
        UIManager.setLookAndFeel(
           UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e)
      {}
   
      System.out.println( "# JREEPAD-NG System Loaded, starting GUI .." );
      
      new JreepadViewer( argHandler.getFileArg(), argHandler.getOptionFileArg() );
   }

   /** Error-tolerant check for Java VM version to comply with this software. 
    *  If clearly fails, program terminates with error message.
    */
   private static void controlJavaVersion ()
   {
      String ver, n[];
      int i, num1, num2, num3;
      
      // get Java version from system properties
      ver = System.getProperty("java.runtime.version");
      if ( ver == null  || ver.isEmpty() )
         ver = System.getProperty("java.version");
      
      // show Java version and default encoding
      System.out.println( "# Java VM " + ver );
      System.out.println( "# Default Encoding: " + getDefaultCharset() );
   
      // verify minimum VM version 1.6
      try {
         i = ver.indexOf('_');
         if ( i > 0 )
            ver = ver.substring( 0, i );
         
         n = ver.split( "[.]" );
         num1 = Integer.parseInt( n[0] ) * 10000;
         num2 = Integer.parseInt( n[1] ) * 100;
         num3 = Integer.parseInt( n[2] );
         i = num1 + num2 + num3;
         
         if ( i < 10600 )
         {
            System.err.println( "*** !! INCOMPATIBLE JAVA VIRTUAL MACHINE !! ***" ); 
            System.err.println( "*** JPWS requires minimum version: 1.6.0" ); 
            System.exit(1);
         }
      }
      catch ( Exception e )
      {e.printStackTrace();}
   }


  /** The currently active default character set of the Java Virtual Machine. */
  public static String getDefaultCharset ()
  {
     return Charset.defaultCharset().name();
  }

  public static String getProgramTitle ()
  {
     return PROGRAM_NAME + " " + PROGRAM_VERSION;
  }
  
  // *************  INNER CLASSES  ********************
  
  private static class ArgumentHandler
  {
     private enum ArgumentType  { file, prefs, lang, country }
     Locale locale;
     String[] args;
     String fileArg;
     String optionFileArg;
     String languageArg;
     String countryArg;
  
  
  ArgumentHandler ( String[] args ) {
     if ( args == null )
        throw new IllegalArgumentException( "args is null" );
     this.args = args;
     digest( args );
  }

  /** The start argument commandline (concatenation of CL-arguments).
   * 
   * @return String, startup argument commandline or empty string if no arguments
   */
  public String getCommandline () {
     StringBuffer b = new StringBuffer();
     for ( String a : args ) {
        b.append( a.concat( " " ) );
     }
     return b.toString();
  }

private void digest (  String[] args ) {
     ArgumentType expectArg = ArgumentType.file; 
     
     for ( String a : args ) {
        if ( a.equalsIgnoreCase( "-p" ) )
           expectArg = ArgumentType.prefs;
        else if ( a.equalsIgnoreCase( "-l" ) )
           expectArg = ArgumentType.lang;
        else if ( a.equalsIgnoreCase( "-c" ) )
           expectArg = ArgumentType.country;
        else if( a.startsWith("-h") || a.startsWith("--h") )
        {
            // show help text and exit
           System.out.println( getProgramTitle().concat( " argument syntax:" ));
           System.out.println( "[filename] [-p [prefsfile]] [-l [language]]");
           System.out.println("   [filename]        Jreepad/treepad file to load on startup");
           System.out.println("   -p [prefsfile]    Preferences from named location instead of default");
           System.out.println("   -l [language]     GUI language instead of default (ISO-639)");
           System.out.println("   -c [country]      GUI country instead of default (ISO-3166)");
           System.out.println();
           System.out.println("Example:");
           System.out.println("  java -jar Jreepad.jar -p /Users/jo/Library/jreepad.pref /Users/jo/Documents/mynotes.hjt");
           System.exit(1);
        }
        else {
           switch ( expectArg ) {
           case file:
              fileArg = a;
              break;
           case prefs:
              optionFileArg = a;
              break;
           case lang:
              languageArg = a;
              break;
           case country:
              countryArg = a;
              break;
           }
        }
     }
  }
  
  /** A start-file argument or <b>null</b> if none was supplied. 
   * 
   * @return String
   */
  public String getFileArg () {
     return fileArg;
  }
  
  /** A program preferences file argument or <b>null</b> if none was supplied. 
   * 
   * @return String
   */
  public String getOptionFileArg () {
     return optionFileArg;
  }
  
  /** Returns a locale representing any locale specific arguments
   * the caller may have supplied. Unspecified elements of the 
   * newly created locale are substituted from the VM default locale.
   * 
   * @return <code>Locale</code> locale implied by arguments 
   *         or <b>null</b> if no locale relevant argument was supplied
   */
  public Locale getLocale () {
     if ( locale != null )
        return locale;
     
     // if we have some locale arguments, construct a new locale
     if ( languageArg != null | countryArg != null ) {
        locale = new Locale( 
              languageArg == null ? Locale.getDefault().getLanguage() : languageArg,
              countryArg == null ? Locale.getDefault().getCountry() : countryArg  );
     }
     return locale;
  }
  
  
  }
  
}
