package com.edusasse.themasterthief;

import java.awt.Graphics2D;

public abstract class Objeto {

	abstract public void DesenhaSe(Graphics2D dbg, int coordenadaX, int coordenadaY);

	abstract public void SimulaSe(long diftime);

}
