package com.edusasse.themasterthief;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.imageio.ImageIO;

public class Menu {

    private static Menu menu = null;
    private static GamePanel gp = null;
    private static Graphics2D dbg = null;
    private static Image fundoMenu = null;
    private static int itemSelecionado = 0;
    private boolean ENTER = false;
    private boolean AUP = false;
    private boolean ADOWN = false;
    private boolean VOLTAR = false;

    private Menu(Graphics2D dbg2) {
        super();
        // ser possivel
        dbg = dbg2;
        try {
            fundoMenu = ImageIO.read(getClass().getClassLoader().getResource("img/menu_back.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Menu getInstanciaMenu(GamePanel _gp) {
        if (menu == null) {
            menu = new Menu(_gp.dbg);
            gp = _gp;
        }
        return menu;
    }

    public void trataTeclas() {
        ENTER = false;
        VOLTAR = false;
        if (gp.AUP && itemSelecionado > 0) {
            itemSelecionado -= 1;
        } else if (gp.ADOWN && itemSelecionado < 2) {
            itemSelecionado += 1;
        } else if (gp.ARIGHT) {
            ENTER = true;
        } else if (gp.ALEFT) {
            VOLTAR = true;
        }
    }

    public void DesenhaSe() throws InterruptedException {
    	dbg.drawImage(fundoMenu, 0, 0, 128, 160, null);
    	dbg.setColor(new Color(50, 0, 0, 200));
    	dbg.fillRect(0, 0, 56, 160);
    	dbg.fillRect(0, 0, 128, 20);
    	dbg.setFont(new Font("Lucida Sans", Font.BOLD, 10));
    	dbg.setColor(Color.ORANGE);
    	dbg.drawString("The Master Thief", 10, 15);


    	if (ENTER) {
    		if (itemSelecionado == 0) {
    			gp.running = true;
    			gp.menu = false;
    		} else if (itemSelecionado == 1) {
    			itemSelecionado = 0;
    			ENTER = false;
    			while (true) {
    				dbg.drawImage(fundoMenu, 0, 0, 128, 160, null);
    				dbg.setColor(new Color(50, 0, 0, 200));
    				dbg.fillRect(0, 0, 56, 160);
    		    	dbg.setFont(new Font("Lucida Sans", Font.BOLD, 10));
    		    	dbg.setColor(Color.ORANGE);
    		    	dbg.drawString("The Master Thief", 10, 15);
    				dbg.setColor(new Color(50, 0, 0, 200));
    				if (ENTER) {
    					if (itemSelecionado == 0) {
    						gp.Difficult = 1;
    						break;
    					} else if (itemSelecionado == 1) {
    						gp.Difficult = 2;
    						break;
    					} else if (itemSelecionado == 2) {
    						gp.Difficult = 3;
    						break;
    					}
    				} else {
    					if (itemSelecionado == 0) {
    						dbg.fillRect(15, 25, 50, 15);
    					} else if (itemSelecionado == 1) {
    						dbg.fillRect(15, 45, 50, 15);
    					} else if (itemSelecionado == 2) {
    						dbg.fillRect(15, 65, 50, 15);
    					}
    				}
    				dbg.setFont(new Font("Lucida Sans", Font.BOLD, 9));
    				dbg.setColor(Color.ORANGE);
    				dbg.drawString("Facil", 20, 35);
    				dbg.drawString("Normal", 20, 55);
    				dbg.drawString("Dificil", 20, 75);
    				gp.repaint();
    				Thread.sleep(25);

    			}
    			itemSelecionado = 0;
    			ENTER = false;
    		}
    		// Entregar-se
    		else if (itemSelecionado == 2) {
  			
                ENTER = false;
    			while (true) {
    				if (ENTER) 
    					break;
    				gp.repaint();
    				Thread.sleep(25);
    			}
    			ENTER = false;		
    		}
    	} else {
    		// Iniciar
    		if (itemSelecionado == 0) {
    			dbg.setColor(new Color(100, 0, 0, 160));
    		} else {
    			dbg.setColor(new Color(200, 0, 0, 160));
    		}
    		dbg.fillRect(15, 25, 90, 15);
    		dbg.setFont(new Font("Lucida Sans", Font.BOLD, 9));
    		dbg.setColor(Color.YELLOW);
    		dbg.drawString("Iniciar", 20, 35);

    		// Dificuldade
    		if (itemSelecionado == 1) {
    			dbg.setColor(new Color(100, 0, 0, 160));
    		} else {
    			dbg.setColor(new Color(200, 0, 0, 160));
    		}
    		dbg.fillRect(15, 55, 90, 15);
    		dbg.setFont(new Font("Lucida Sans", Font.BOLD, 9));
    		dbg.setColor(Color.YELLOW);
    		dbg.drawString("Dificuldade", 20, 65);



    	}
    }
}