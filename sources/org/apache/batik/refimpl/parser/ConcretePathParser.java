/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.parser;

import java.io.Reader;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathHandler;
import org.apache.batik.parser.PathParser;

/**
 * This class implements an event-based parser for the SVG path's d
 * attribute values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ConcretePathParser
    extends    NumberParser
    implements PathParser {
    /**
     * The path handler used to report parse events.
     */
    protected PathHandler pathHandler;

    /**
     * Whether the last character was a 'e' or 'E'.
     */
    protected boolean eRead;

    /**
     * Creates a new PathParser.
     */
    public ConcretePathParser() {
	pathHandler = DefaultPathHandler.INSTANCE;
    }

    /**
     * Allows an application to register a path handler.
     *
     * <p>If the application does not register a handler, all
     * events reported by the parser will be silently ignored.
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the parser must begin using the new
     * handler immediately.</p>
     * @param handler The transform list handler.
     */
    public void setPathHandler(PathHandler handler) {
	pathHandler = handler;
    }

    /**
     * Returns the path handler in use.
     */
    public PathHandler getPathHandler() {
	return pathHandler;
    }

    /**
     * Parses the given reader.
     */
    public void parse(Reader r) throws ParseException {
	initialize(r);

	pathHandler.startPath();

	read();
	loop: for (;;) {
	    switch (current) {
	    case 0xD:
	    case 0xA:
	    case 0x20:
	    case 0x9:
		read();
		break;
	    case 'z':
	    case 'Z':
		read();
		pathHandler.closePath();
		break;
	    case 'm':
		parsem();
	    case 'l':
		parsel();
		break;
	    case 'M':
		parseM();
	    case 'L':
		parseL();
		break;
	    case 'h':
		parseh();
		break;
	    case 'H':
		parseH();
		break;
	    case 'v':
		parsev();
		break;
	    case 'V':
		parseV();
		break;
	    case 'c':
		parsec();
		break;
	    case 'C':
		parseC();
		break;
	    case 'q':
		parseq();
		break;
	    case 'Q':
		parseQ();
		break;
	    case 's':
		parses();
		break;
	    case 'S':
		parseS();
		break;
	    case 't':
		parset();
		break;
	    case 'T':
		parseT();
		break;
	    case 'a':
		parsea();
		break;
	    case 'A':
		parseA();
		break;
	    default:
		if (current == -1) {
		    break loop;
		}
		reportError("character.unexpected",
			    new Object[] { new Integer(current) });
		skipSubPath();
	    }
	}

	skipSpaces();
	if (current != -1) {
	    reportError("end.of.stream.expected",
			new Object[] { new Integer(current) });
	}

	pathHandler.endPath();
    }    

    /**
     * Parses a 'm' command.
     */
    protected void parsem() throws ParseException {
	read();
	skipSpaces();

	try {
	    float x = parseFloat();
	    skipCommaSpaces();
	    float y = parseFloat();

	    pathHandler.movetoRel(x, y);
	} catch (NumberFormatException e) {
	    reportError("float.format", new Object[] { getBufferContent() });
	    skipSubPath();
	}
    }

    /**
     * Parses a 'l' command.
     */
    protected void parsel() throws ParseException {
	if (current == 'l') {
	    read();
	}
	skipSpaces();
        for (;;) {
	    switch (current) {
	    case '+': case '-':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
		try {
		    float x = parseFloat();
		    skipCommaSpaces();
		    float y = parseFloat();

		    pathHandler.linetoRel(x, y);
		} catch (NumberFormatException e) {
		    reportError("float.format",
				new Object[] { getBufferContent() });
		    skipSubPath();
		    return;
		}
		break;
	    default:
		return;
	    }
	    skipCommaSpaces();
	}
    }

    /**
     * Parses a 'M' command.
     */
    protected void parseM() throws ParseException {
	read();
	skipSpaces();

	try {
	    float x = parseFloat();
	    skipCommaSpaces();
	    float y = parseFloat();
	    
	    pathHandler.movetoAbs(x, y);
	} catch (NumberFormatException e) {
	    reportError("float.format", new Object[] { getBufferContent() });
	    skipSubPath();
	}	
    }

    /**
     * Parses a 'L' command.
     */
    protected void parseL() throws ParseException {
	if (current == 'L') {
	    read();
	}
	skipSpaces();
        for (;;) {
	    switch (current) {
	    case '+': case '-':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
		try {
		    float x = parseFloat();
		    skipCommaSpaces();
		    float y = parseFloat();

		    pathHandler.linetoAbs(x, y);
		} catch (NumberFormatException e) {
		    reportError("float.format",
                                new Object[] { getBufferContent() });
		    skipSubPath();
		    return;
		}
		break;
	    default:
		return;
	    }
	    skipCommaSpaces();
	}
    }

    /**
     * Parses a 'h' command.
     */
    protected void parseh() throws ParseException {
	read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    case '+': case '-':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
		try {
		    float x = parseFloat();
		    pathHandler.linetoHorizontalRel(x);
		} catch (NumberFormatException e) {
		    reportError("float.format",
				new Object[] { getBufferContent() });
		    skipSubPath();
		    return;
		}
		break;
	    default:
		return;
	    }
	    skipCommaSpaces();
	}
    }

    /**
     * Parses a 'H' command.
     */
    protected void parseH() throws ParseException {
	read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    case '+': case '-':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
		try {
		    float x = parseFloat();
		    pathHandler.linetoHorizontalAbs(x);
		} catch (NumberFormatException e) {
		    reportError("float.format",
				new Object[] { getBufferContent() });
		    skipSubPath();
		    return;
		}
		break;
	    default:
		return;
	    }
	    skipCommaSpaces();
	}
    }

    /**
     * Parses a 'v' command.
     */
    protected void parsev() throws ParseException {
	read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    case '+': case '-':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
		try {
		    float x = parseFloat();
		    pathHandler.linetoVerticalRel(x);
		} catch (NumberFormatException e) {
		    reportError("float.format",
				new Object[] { getBufferContent() });
		    skipSubPath();
		    return;
		}
		break;
	    default:
		return;
	    }
	    skipCommaSpaces();
	}
    }

    /**
     * Parses a 'V' command.
     */
    protected void parseV() throws ParseException {
	read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    case '+': case '-':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
		try {
		    float x = parseFloat();
		    pathHandler.linetoVerticalAbs(x);
		} catch (NumberFormatException e) {
		    reportError("float.format",
				new Object[] { getBufferContent() });
		    skipSubPath();
		    return;
		}
		break;
	    default:
		return;
	    }
	    skipCommaSpaces();
	}
    }

    /**
     * Parses a 'c' command.
     */
    protected void parsec() throws ParseException {
	read();
	skipSpaces();
	
	for (;;) {
	    switch (current) {
	    default:
		return;
	    case '+': case '-':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
	    }

	    try {
		float x1 = parseFloat();
		skipCommaSpaces();
		float y1 = parseFloat();
		skipCommaSpaces();
		float x2 = parseFloat();
		skipCommaSpaces();
		float y2 = parseFloat();
		skipCommaSpaces();
		float x = parseFloat();
		skipCommaSpaces();
		float y = parseFloat();

		pathHandler.curvetoCubicRel(x1, y1, x2, y2, x, y);
	    } catch (NumberFormatException e) {
		reportError("float.format",
			    new Object[] { getBufferContent() });
		skipSubPath();
		return;
	    }
	    skipCommaSpaces();
	}
    }		

    /**
     * Parses a 'C' command.
     */
    protected void parseC() throws ParseException {
	read();
	skipSpaces();
	
	for (;;) {
	    switch (current) {
	    default:
		return;
	    case '+': case '-':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
	    }

	    try {
		float x1 = parseFloat();
		skipCommaSpaces();
		float y1 = parseFloat();
		skipCommaSpaces();
		float x2 = parseFloat();
		skipCommaSpaces();
		float y2 = parseFloat();
		skipCommaSpaces();
		float x = parseFloat();
		skipCommaSpaces();
		float y = parseFloat();

		pathHandler.curvetoCubicAbs(x1, y1, x2, y2, x, y);
	    } catch (NumberFormatException e) {
		reportError("float.format",
			    new Object[] { getBufferContent() });
		skipSubPath();
		return;
	    }
	    skipCommaSpaces();
	}
    }

    /**
     * Parses a 'q' command.
     */
    protected void parseq() throws ParseException {
	read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    default:
		return;
	    case '+': case '-':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
	    }

	    try {
		float x1 = parseFloat();
		skipCommaSpaces();
		float y1 = parseFloat();
		skipCommaSpaces();
		float x = parseFloat();
		skipCommaSpaces();
		float y = parseFloat();

		pathHandler.curvetoQuadraticRel(x1, y1, x, y);
	    } catch (NumberFormatException e) {
		reportError("float.format",
			    new Object[] { getBufferContent() });
		skipSubPath();
		return;
	    }
	    skipCommaSpaces();
	}
    }

    /**
     * Parses a 'Q' command.
     */
    protected void parseQ() throws ParseException {
	read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    default:
		return;
	    case '+': case '-':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
	    }

	    try {
		float x1 = parseFloat();
		skipCommaSpaces();
		float y1 = parseFloat();
		skipCommaSpaces();
		float x = parseFloat();
		skipCommaSpaces();
		float y = parseFloat();

		pathHandler.curvetoQuadraticAbs(x1, y1, x, y);
	    } catch (NumberFormatException e) {
		reportError("float.format",
			    new Object[] { getBufferContent() });
		skipSubPath();
		return;
	    }
	    skipCommaSpaces();
	}
    }

    /**
     * Parses a 's' command.
     */
    protected void parses() throws ParseException {
	read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    default:
		return;
	    case '+': case '-':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
	    }
	    
	    try {
		float x2 = parseFloat();
		skipCommaSpaces();
		float y2 = parseFloat();
		skipCommaSpaces();
		float x = parseFloat();
		skipCommaSpaces();
		float y = parseFloat();

		pathHandler.curvetoCubicSmoothRel(x2, y2, x, y);
	    } catch (NumberFormatException e) {
		reportError("float.format",
			    new Object[] { getBufferContent() });
		skipSubPath();
		return;
	    }
	    skipCommaSpaces();
	}
    }		

    /**
     * Parses a 'S' command.
     */
    protected void parseS() throws ParseException {
	read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    default:
		return;
	    case '+': case '-':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
	    }
	    
	    try {
		float x2 = parseFloat();
		skipCommaSpaces();
		float y2 = parseFloat();
		skipCommaSpaces();
		float x = parseFloat();
		skipCommaSpaces();
		float y = parseFloat();

		pathHandler.curvetoCubicSmoothAbs(x2, y2, x, y);
	    } catch (NumberFormatException e) {
		reportError("float.format",
			    new Object[] { getBufferContent() });
		skipSubPath();
		return;
	    }
	    skipCommaSpaces();
	}
    }		

    /**
     * Parses a 't' command.
     */
    protected void parset() throws ParseException {
	read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    default:
		return;
	    case '+': case '-':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
	    }

	    try {
		float x = parseFloat();
		skipCommaSpaces();
		float y = parseFloat();

		pathHandler.curvetoQuadraticSmoothRel(x, y);
	    } catch (NumberFormatException e) {
		reportError("float.format",
			    new Object[] { getBufferContent() });
		skipSubPath();
		return;
	    }
	    skipCommaSpaces();
	}		
    }

    /**
     * Parses a 'T' command.
     */
    protected void parseT() throws ParseException {
	read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    default:
		return;
	    case '+': case '-':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
	    }

	    try {
		float x = parseFloat();
		skipCommaSpaces();
		float y = parseFloat();

		pathHandler.curvetoQuadraticSmoothAbs(x, y);
	    } catch (NumberFormatException e) {
		reportError("float.format",
			    new Object[] { getBufferContent() });
		skipSubPath();
		return;
	    }
	    skipCommaSpaces();
	}		
    }

    /**
     * Parses a 'a' command.
     */
    protected void parsea() throws ParseException {
	read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    default:
		return;
	    case '+': case '-':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
	    }

	    try {
		float rx = parseFloat();
		skipCommaSpaces();
		float ry = parseFloat();
		skipCommaSpaces();
		float ax = parseFloat();
		skipCommaSpaces();
		
		boolean laf;
		switch (current) {
		case '0':
		    laf = false;
		    break;
		case '1':
		    laf = true;
		    break;
		default:
		    reportError("character.unexpected",
				new Object[] { new Integer(current) });
		    skipSubPath();
		    return;
		}

		read();
		skipCommaSpaces();

		boolean sf;
		switch (current) {
		case '0':
		    sf = false;
		    break;
		case '1':
		    sf = true;
		    break;
		default:
		    reportError("character.unexpected",
				new Object[] { new Integer(current) });
		    skipSubPath();
		    return;
		}

		read();
		skipCommaSpaces();

		float x = parseFloat();
		skipCommaSpaces();
		float y = parseFloat();

		pathHandler.arcRel(rx, ry, ax, laf, sf, x, y);
	    } catch (NumberFormatException e) {
		reportError("float.format",
                            new Object[] { getBufferContent() });
		skipSubPath();
		return;
	    }
	    skipCommaSpaces();
	}
    }

    /**
     * Parses a 'A' command.
     */
    protected void parseA() throws ParseException {
	read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    default:
		return;
	    case '+': case '-':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
	    }

	    try {
		float rx = parseFloat();
		skipCommaSpaces();
		float ry = parseFloat();
		skipCommaSpaces();
		float ax = parseFloat();
		skipCommaSpaces();

		boolean laf;
		switch (current) {
		case '0':
		    laf = false;
		    break;
		case '1':
		    laf = true;
		    break;
		default:
		    reportError("character.unexpected",
				new Object[] { new Integer(current) });
		    skipSubPath();
		    return;
		}

		read();
		skipCommaSpaces();

		boolean sf;
		switch (current) {
		case '0':
		    sf = false;
		    break;
		case '1':
		    sf = true;
		    break;
		default:
		    reportError("character.unexpected",
				new Object[] { new Integer(current) });
		    skipSubPath();
		    return;
		}

		read();
		skipCommaSpaces();
		float x = parseFloat();
		skipCommaSpaces();
		float y = parseFloat();

		pathHandler.arcAbs(rx, ry, ax, laf, sf, x, y);
	    } catch (NumberFormatException e) {
		reportError("float.format",
			    new Object[] { getBufferContent() });
		skipSubPath();
		return;
	    }
	    skipCommaSpaces();
	}
    }

    /**
     * Skips a sub-path.
     */
    protected void skipSubPath() throws ParseException {
	for (;;) {
	    switch (current) {
	    case 'm':
	    case 'M':
		return;
	    default:
		if (current == -1) {
		    return;
		}
		read();
	    }
	}
    }

    /**
     * Implements {@link NumberParser#readNumber()}.
     */
    protected void readNumber() throws ParseException {
	bufferSize = 0;
	bufferize();
	eRead = false;
        for (;;) {
	    read();
	    switch (current) {
	    case 0x20:
	    case 0x9:
	    case 0xD:
	    case 0xA:
	    case 'm': case 'M':
	    case 'c': case 'C':
	    case 's': case 'S':
	    case 'q': case 'Q':
	    case 't': case 'T':
	    case 'l': case 'L':
	    case 'h': case 'H':
	    case 'v': case 'V':
	    case 'a': case 'A':
	    case 'z': case 'Z':
	    case ',':
		return;
	    case 'e': case 'E':
		eRead = true;
		bufferize();
		break;
	    case '+':
	    case '-':
		if (!eRead) {
		    return;
		}
	    default:
		if (current == -1) {
		    return;
		}
		eRead = false;
		bufferize();
	    }
	}
    }
}
