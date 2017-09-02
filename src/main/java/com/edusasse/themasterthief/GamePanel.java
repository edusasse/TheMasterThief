package com.edusasse.themasterthief;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {
    // Largura da TELA

	private static final long serialVersionUID = 1154951129324530075L;
	public static final int PWIDTH = 128;
    // Altura da TELA
    public static final int PHEIGHT = 160;
    // Variavel 'running' que mantem o jogo rodando enquanto for verdadeira
    public boolean running = false;
    // Variavel 'menu' que mantem o menu rodando enquanto for verdadeira
    public boolean menu = false;
    // Variavel 'gameOver' diz que jogo terminou com o personagem derrotado
    private boolean gameOver = false;
    // Fase do jogo
    private int fase = 0;
    //
    boolean auxAlguemPerseguindo = false;

    // Area para desenho
    public Graphics2D dbg;
    private Image dbImage = null;

    // frames per second, pra ser exibido na tela
    int FPS, SFPS;
    long SegundoAtual = 0;
    long NovoSegundo = 0;
    private byte itemSelecionado = 0;

    // Atributos do Jogo
    Vector ListaObjetos; // lista de naves

    Vector imagens = new Vector();
    Vector ListaExplosoes;
    Vector inventario = new Vector();

    // Representacao grafica dos objetos do jogo, os arrays correspondem a
    // sprites animados
    public Image Ladrao[];
    private byte qtdCarve = 0;
    public Image carne;
    private boolean temChave = false;
    public Image chaves;
    public Image cao[];
    public Image Inimigos[][];
    // Personagem
    Sprite Personagem;
    // Mapa do Jogo
    TileMap MAPA;
    // Radio de Visao dos inimigos
    private int raioDeVisao = 40;

    // Fonte utilizada no JOOGO
    Font FonteJogo = new Font(null, Font.BOLD, 10);

    // Novo X e Y
    float novoX, novoY;
    // Variavel auxiliar que troca as Persan do jogador
    int auxTrocaPersonagem;

    // Troca as pernas dos inimigos
    int auxTrocaInimigo;

    // Codigos para eventos de teclado
    boolean AUP, ADOWN, ALEFT, ARIGHT, FIRE, PAUSE, ESCAPE, LETRA_I, pausado, leBotao;

    // Tempo de Impacto
    int TimeImpac;
    // Se Personagem foi atingido
    boolean Atingido = false;
    // Indice de Dificuldade
    int Difficult;

    // Pontos do personagem
    int Pontos = 0;
    // Numero de Vidas do Personagem
    int Vidas = 4;
    // Gerador de numeros randomicos
    private static Random rnd = new Random();
    // CONSTANTES
    private byte CAO_DIR_1 = 0,  CAO_DIR_2 = 1,  CAO_ESQ_1 = 2,  CAO_ESQ_2 = 3,  CAO_TRA_1 = 4,  CAO_TRA_2 = 5,  CAO_FRE_1 = 6,  CAO_FRE_2 = 7;
    // Clip
    MP3 somLatido;
    // ** Cria instancia */

    public GamePanel() {

        this.setBackground(Color.white); // Pinta o fundo de BRANCO

        this.setPreferredSize(new Dimension(this.PWIDTH, this.PHEIGHT)); // Seta
        // para
        // tela
        // o
        // tamanho
        // definidos

        this.setFocusable(true); // Seta a tela como focavel

        this.requestFocus(); // Seta foco na tela para a leitura do teclado
        // ser possivel

        // Adiciona um Key Listner
        this.addKeyListener(new KeyAdapter() {
            // Listen for esc, q, end, ctrl-c

            public void keyPressed(KeyEvent e) {
                TratadorDeTeclado(e);
            }

            // Tecla solta
            public void keyReleased(KeyEvent e) {
                TratadorDeTecladoReleased(e);
            }
        });

        // Qual a posicao inicial do personagem?
        this.novoX = 0;
        novoY = 0;
        this.auxTrocaPersonagem = 0;
        this.auxTrocaInimigo = 0;
        // Sem movimento inicial
        AUP = false;
        ADOWN = false;
        ALEFT = false;
        ARIGHT = false;
        leBotao = true;
        // Personagem
        this.Ladrao = new Image[12];
        // Cao, inimigo inicial
        this.cao = new Image[8]; // 8 imagens

        try {
            this.carne = ImageIO.read(getClass().getClassLoader().getResource("img/carne.png"));
            this.chaves = ImageIO.read(getClass().getClassLoader().getResource("img/chaves.png"));

            this.Ladrao[0] = ImageIO.read(getClass().getClassLoader().getResource("img/costas_1.png"));
            this.Ladrao[1] = ImageIO.read(getClass().getClassLoader().getResource("img/costas_2.png"));
            this.Ladrao[2] = ImageIO.read(getClass().getClassLoader().getResource("img/costas_3.png"));
            this.Ladrao[3] = ImageIO.read(getClass().getClassLoader().getResource("img/frente_1.png"));
            this.Ladrao[4] = ImageIO.read(getClass().getClassLoader().getResource("img/frente_2.png"));
            this.Ladrao[5] = ImageIO.read(getClass().getClassLoader().getResource("img/frente_3.png"));
            this.Ladrao[6] = ImageIO.read(getClass().getClassLoader().getResource("img/esquerda_1.png"));
            this.Ladrao[7] = ImageIO.read(getClass().getClassLoader().getResource("img/esquerda_2.png"));
            this.Ladrao[8] = ImageIO.read(getClass().getClassLoader().getResource("img/esquerda_3.png"));
            this.Ladrao[9] = ImageIO.read(getClass().getClassLoader().getResource("img/direita_1.png"));
            this.Ladrao[10] = ImageIO.read(getClass().getClassLoader().getResource("img/direita_2.png"));
            this.Ladrao[11] = ImageIO.read(getClass().getClassLoader().getResource("img/direita_3.png"));
            // Cao
            // cao = ImageIO.read(getClass().getClassLoader().getResource("img/cao.png"));
            cao[CAO_DIR_1] = ImageIO.read(getClass().getClassLoader().getResource("img/cao_dir1.png"));
            cao[CAO_DIR_2] = ImageIO.read(getClass().getClassLoader().getResource("img/cao_dir2.png"));
            cao[CAO_ESQ_1] = ImageIO.read(getClass().getClassLoader().getResource("img/cao_esq1.png"));
            cao[CAO_ESQ_2] = ImageIO.read(getClass().getClassLoader().getResource("img/cao_esq2.png"));
            cao[CAO_FRE_1] = ImageIO.read(getClass().getClassLoader().getResource("img/cao_fre1.png"));
            cao[CAO_FRE_2] = ImageIO.read(getClass().getClassLoader().getResource("img/cao_fre2.png"));
            cao[CAO_TRA_1] = ImageIO.read(getClass().getClassLoader().getResource("img/cao_tra1.png"));
            cao[CAO_TRA_2] = ImageIO.read(getClass().getClassLoader().getResource("img/cao_tra2.png"));

        } catch (IOException e) {
            System.out.println("Load Image error:");
        }
        try {
            // Cria o Mapa
            this.MAPA = new TileMap(this);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // Cria a lista dos objetos pertinentes ao jogo
        this.ListaObjetos = new Vector();
        // this.ListaTiros = new Vector();
        // this.ListaExplosoes = new Vector();

        // Carrega Personagem
        Personagem = new Sprite(this.Ladrao[0], 80, 78, 0, false,
                this.Ladrao[0].getWidth(null), this.Ladrao[0].getHeight(null),
                1, 1);
        Personagem.FrameTime = 200;

        // Carrega Inimigo - CAO
        Sprite cao1 = new Sprite(cao[0], 90, 140, 100, false, 19, 18, 1, 1);
        Sprite cao2 = new Sprite(cao[0], 90, 140, 0, false, 19, 18, 1, 1);
        cao1.VelY = .5f; // velocidade do inimigo

        cao1.VelX = .5f; // velocidade do inimigo

        cao1.Aceleracao = +1; // +1 vai pra direita, -1 vai pra esquerda

        cao2.VelY = .5f; // velocidade do inimigo

        cao2.VelX = .5f; // velocidade do inimigo

        cao2.Aceleracao = +1; // +1 vai pra direita, -1 vai pra esquerda
        // this.ListaObjetos.add(cao1);

        this.ListaObjetos.add(cao2);
        //this.ListaObjetos.add(cao1);

        //Applet.newAudioClip(new File("somFundo.mp3").toURL()).loop();
        MP3 mp3 = new MP3(getClass().getClassLoader().getResource("mp3/background.mp3").getFile());
        mp3.play();
        this.somLatido = new MP3(getClass().getClassLoader().getResource("mp3/ost.mp3").getFile());

    }

    // ** THREAD */
    // ##########################################################
    @Override
    public void addNotify() {
        super.addNotify(); // creates the peer

        this.startGame(); // start the thread

    }

    // ** Metodo de continuidade do Jogo */
    public void run() {
        long DifTime, TempoAnterior;

        DifTime = 0;
        TempoAnterior = System.currentTimeMillis();
        SegundoAtual = (long) (TempoAnterior / 1000);

        this.menu = true;
        this.running = false;

        // Menu do Jogo
        while (menu) {
            // Desenha no buffer
            if (dbImage == null) { // create the buffer

                dbImage = createImage(PWIDTH, PHEIGHT);
                if (dbImage == null) {
                    System.out.println("dbImage is null");
                    return;
                } else {
                    dbg = (Graphics2D) dbImage.getGraphics();
                }
            }
            try {
                // Renderiza o mapa
                Menu.getInstanciaMenu(this).DesenhaSe();
                super.repaint();
                Thread.sleep(25); // sleep a bit 

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Carrega os itens para a fase
        this.carregaInventarioDaFase(this.fase);

        
        // Laco de execucao do jogo
        while (running) {
            // Jogo roda se nao ocorrer game over e se a tecla de PAUSA nao for pressionada
            if (!PAUSE && !gameOver) {
                // Simula
                this.gameUpdate(DifTime);
                // Pinta no Buffer
                this.gameRender();
                // Pinta o Buffer
                super.repaint();
                try {
                    // Dorme
                    Thread.sleep(25);
                } catch (InterruptedException ex) {
                }

                // Calculo do Difftime
                {
                    DifTime = System.currentTimeMillis() - TempoAnterior;
                    TempoAnterior = System.currentTimeMillis();
                    NovoSegundo = (long) (TempoAnterior / 1000);

                    if (NovoSegundo == SegundoAtual) {
                        SFPS += 2;
                    } else {
                        FPS = SFPS;
                        SegundoAtual = NovoSegundo;
                        SFPS = 1;
                    }
                } // Fim do calculo de DiffTime

            } else // Se o botao de PAUSA for pressionado e o jogo estiver rodandom ou seja, que nao ja esteha pausado
            if (PAUSE && !pausado) {
                dbg.setColor(new Color(0, 0, 0, 100));
                dbg.fillRect(0, 0, 128, 160);
                dbg.setColor(Color.WHITE);
                dbg.setFont(FonteJogo);
                dbg.drawString("PAUSE", 45, 84);
                super.repaint();
                // E pausa o game
                pausado = true;
            }
        }
    }


// ** Comeca o jogo */
    private void startGame() {
        if (!this.running) {
            new Thread(this).start();
        }

    }

    // ** Para o jogo */
    public void stopGame() {
        this.running = false;
    }

// ** LOGICA */
// ##########################################################

// ** Atualiza propriedades dos elementos do jogo */
    private void gameUpdate(long DiffTime) {
        // Aumenta tempo de Impacto baseado no DiffTime
        TimeImpac += DiffTime;
        // Dificuldade
        Difficult =
                1 + (TimeImpac / 5000);

        // Caso personagem nao tenha sido atingido
        if (!this.Atingido) {
            // Troca Pernas
            this.trocaDePernas();
            // Personagem anda
            this.botaoPressionado();
            // Verifica como o inimigo deve se comportar
            this.acaoDoInimigo();
            // Calcula colisao de personagem com inimigos
            this.calculaColisao();

            // Simula Personagem
            this.Personagem.SimulaSe(DiffTime);
            // Simula Lista de Objetos
            // System.out.println("=============================================================={");
            // System.out.println("Deslocou o MAPA: " + this.MAPA.isMapaDeslocou());
            /// System.out.println("Personagem: [ " + Personagem.X + " ; " + (Personagem.Y) + " ]");
            for (int i = 0; i <
                    ListaObjetos.size(); i++) {
                ((Sprite) ListaObjetos.elementAt(i)).SimulaSe(DiffTime);
            //  System.out.println("Perseguindo: " + ((Sprite) ListaObjetos.get(i)).estaPerseguindo);
            //  System.out.println("Cachorro: " + i + " [ " + ((Sprite) ListaObjetos.get(i)).X + " ; " + (((Sprite) ListaObjetos.get(i)).Y) + " ]");
            }

        //System.out.println("==============================================================}");*/


        }
        if (this.LETRA_I == true) {
            this.itemSelecionado = (byte) ((this.itemSelecionado + 1) % this.inventario.size());
            LETRA_I = false;
        }
        

    }

    // ** Faz a troca das pernas */
    private void trocaDePernas() {
        if (this.auxTrocaPersonagem == 0) {
            this.auxTrocaPersonagem = 1;
        } else if (this.auxTrocaPersonagem == 1) {
            this.auxTrocaPersonagem = 2;
        } else {
            this.auxTrocaPersonagem = 0;
        }

    }

    // ** Botao pressionado */
    private void botaoPressionado() {
        if (leBotao == true) {
            if (this.ALEFT) {
                this.novoX--;
                this.Personagem.setCharset(this.Ladrao[6 + this.auxTrocaPersonagem]);
            }

            if (ARIGHT) {
                this.novoX++;
                this.Personagem.setCharset(this.Ladrao[9 + this.auxTrocaPersonagem]);
            }

            if (AUP) {
                this.novoY--;
                this.Personagem.setCharset(this.Ladrao[0 + this.auxTrocaPersonagem]);
            }

            if (ADOWN) {
                this.novoY++;
                this.Personagem.setCharset(this.Ladrao[3 + this.auxTrocaPersonagem]);
            }
// leBotao = false;

            if (this.auxTrocaInimigo == 0) {
                this.auxTrocaInimigo = 1;
            } else {
                this.auxTrocaInimigo = 0;
            }

        } else {
            leBotao = true;
        }

    }

    // ** Retorna verdadeiro quando o personagem esta na area de visa de seu
    // inimigo */
    private void acaoDoInimigo() {
        for (int i = 0; i <
                ListaObjetos.size(); i++) {
            // Pega o inimigo corrente
            Sprite inimigo = ((Sprite) ListaObjetos.elementAt(i));
            Double x1 = inimigo.X;
            Double y1 = inimigo.Y;
            Double x2 = Personagem.X;
            Double y2 = Personagem.Y;
            // 'd' contera a distancia entre personagem e o inimigo em questao
            Double d = Math.sqrt(Math.pow(
                    ((this.MAPA.getCoordenadaX() + x1) - (this.MAPA.getCoordenadaX() + x2)), 2) + Math.pow(((this.MAPA.getCoordenadaX() + y1) - (this.MAPA.getCoordenadaX() + y2)), 2));
            // Caso 'd' for menor ou igual ao RAIO DE VISAO dos inimigos
            // System.out.println("d: " + d);

            if (d <= this.raioDeVisao) {
                // Seta como perseguindo
                inimigo.estaPerseguindo = true;
                // Persegue personagem
                this.fazInimigoPerseguirV2(inimigo);
            } else {
                // Inimigos andam aleatoriamente
                this.fazInimigosAndarem();
                // Seta como perseguindo
                inimigo.estaPerseguindo = false;
            }

        }
    }

    // ** Faz inimigos perseguirem */
    private void fazInimigoPerseguirV2(Sprite inimigo) {
	   	
        // Troca Pernas
        inimigo.trocaPernas = !inimigo.trocaPernas;

        if (this.MAPA.isMapaDeslocou()) {
            return;

        // X -----------
        }

        if ((inimigo.X) < (Personagem.X)) {
            inimigo.X += inimigo.VelX;
            if (inimigo.trocaPernas) {
                inimigo.setCharset(cao[this.CAO_DIR_1]);
            } else {
                inimigo.setCharset(cao[this.CAO_DIR_2]);
            }

        } else {
            inimigo.X -= inimigo.VelX;

            if (inimigo.trocaPernas) {
                inimigo.setCharset(cao[this.CAO_ESQ_1]);
            } else {
                inimigo.setCharset(cao[this.CAO_ESQ_2]);
            }

        }
        // Y -----------
        if ((inimigo.Y) < (Personagem.Y)) {
            inimigo.Y += inimigo.VelY;
            if (inimigo.trocaPernas) {
                inimigo.setCharset(cao[this.CAO_FRE_1]);
            } else {
                inimigo.setCharset(cao[this.CAO_FRE_2]);
            }

        } else {
            inimigo.Y -= inimigo.VelY;
            if (inimigo.trocaPernas) {
                inimigo.setCharset(cao[this.CAO_TRA_1]);
            } else {
                inimigo.setCharset(cao[this.CAO_TRA_2]);
            }

        }

        if (inimigo.Y - 3 <= Personagem.Y && inimigo.Y + 3 >= Personagem.Y) {
            if (inimigo.X < Personagem.X) {
                if (inimigo.trocaPernas) {
                    if (inimigo.trocaPernas) {
                        inimigo.setCharset(cao[this.CAO_DIR_1]);
                    } else {
                        inimigo.setCharset(cao[this.CAO_DIR_2]);
                    }

                } else {
                    if (inimigo.trocaPernas) {
                        inimigo.setCharset(cao[this.CAO_ESQ_1]);
                    } else {
                        inimigo.setCharset(cao[this.CAO_ESQ_2]);
                    }

                }
            }
        } else if (inimigo.X + 8 >= Personagem.X && inimigo.X - 8 <= Personagem.X) {
            if (inimigo.Y < Personagem.Y) {
                if (inimigo.trocaPernas) {
                    if (inimigo.trocaPernas) {
                        inimigo.setCharset(cao[this.CAO_TRA_1]);
                    } else {
                        inimigo.setCharset(cao[this.CAO_TRA_2]);
                    }

                } else {
                    if (inimigo.trocaPernas) {
                        inimigo.setCharset(cao[this.CAO_FRE_1]);
                    } else {
                        inimigo.setCharset(cao[this.CAO_FRE_2]);
                    }

                }
            }
        }
    }

    // ** Faz inimigos perseguirem */
	/*
     * private void fazInimigoPerseguir(Sprite inimigo){ // Troca Pernas
     * inimigo.trocaPernas = !inimigo.trocaPernas;
     * 
     * Double x1 = inimigo.X; Double y1 = inimigo.Y; Double x2 = Personagem.X;
     * Double y2 = Personagem.Y; // Coeficiente Angular Double m = (y2 - y1)/(x2 -
     * x1); // X da funcao Double X = m; // Funcao Y Double y0 = (-1)*( (m *
     * (x2))-y2 ); // Funcao X Double x0 = (((m*(x2))-y2)/m);
     *  // Se inimigo nao COLIDIR
     * //if(!MAPA.isColisionAt((int)inimigo.X+(inimigo.Largura/2),
     * (int)inimigo.Y)) { { // Se inimigo esta abaixo do Personagem no eixo Y if
     * (inimigo.Y <= Personagem.Y) { // Se inimigo tem posicao X menor que o
     * personagem if (inimigo.X < Personagem.X){ // Inimigo incrementa X
     * inimigo.X+=inimigo.VelX; // Direita >>
     * inimigo.setCharset(cao[this.CAO_DIR_1]); } else { // Caso contreario
     * decrementa inimigo.X-=inimigo.VelX; // Esquerda <<
     * inimigo.setCharset(cao[this.CAO_ESQ_1]); } // Calcula a POSICAO 'y'
     * baseado na nova posicao 'x' - se movimenta na reta. inimigo.Y = (y0 +
     * (inimigo.X)*X);
     *  } else // Se o inimigo estiver acima do personagem no eixo Y if
     * (inimigo.Y > Personagem.Y) { // Caso estiver acima desloca-se para baixo
     * inimigo.Y-=inimigo.VelY; // Se inimigo tem posicao X menor que o
     * personagem if (inimigo.X < Personagem.X){ // Inimigo incrementa X
     * inimigo.X+=inimigo.VelX; // Direita >>
     * inimigo.setCharset(cao[this.CAO_DIR_1]); } else { // Caso contreario
     * decrementa inimigo.X-=inimigo.VelX; // Esquerda <<
     * inimigo.setCharset(cao[this.CAO_ESQ_1]); } } } //System.out.println("x1: " +
     * x1 + " y1: " + y1 + " | x2: " + x2 + " y2: " + y2 + " m: " + m); }
     */

    // ** Faz inimigos andarem */
    private void fazInimigosAndarem() {
   	
    	// Para toda lista de inimigos
        for (int i = 0; i <
                ListaObjetos.size(); i++) {
            // Pega o inimigo corrente
            Sprite inimigo = ((Sprite) ListaObjetos.elementAt(i));
    
        	// se inimigo tocar na carne
        	for(int j  = 0; j < imagens.size();j++) {
        		Imagem item = ((Imagem)imagens.elementAt(j));
        		if(inimigo.X-8 <= item.X && inimigo.X+8 >= item.X) {       			
            		if(inimigo.Y-8 <= item.Y && inimigo.Y+8 >= item.Y) {       			
        				ListaObjetos.removeElementAt(i);
        			}           	
        		}
        	}
            
            // Troca Pernas
            inimigo.trocaPernas = !inimigo.trocaPernas;

            // Se nao se COLIDIR
            if (!MAPA.isColisionAt((int) inimigo.X + (inimigo.Largura / 2),
                    (int) inimigo.Y)) {
                // Personagem � tentado a mudar de direcao no eixo oposto
                this.tentaInimigoAMudarEixo(inimigo);
                // Caso o inimigo esteja se movimentando no EIXO 'X'
                if (inimigo.inX) {
                    // Troca pernas
                    if (inimigo.trocaPernas) // Opcao 1
                    {
                        inimigo.setCharset(cao[this.CAO_DIR_1]);
                    } else // Opcao 2
                    {
                        inimigo.setCharset(cao[this.CAO_DIR_2]);
                    // Movimenta inimigo no eixo 'X'
                    }

                    inimigo.X += inimigo.VelX;
                // Caso o inimifo esteja se movimentando no EIXO 'Y'
                } else if (!inimigo.inX) {
                    // Troca pernas
                    if (inimigo.trocaPernas) // Opcao 1
                    {
                        inimigo.setCharset(cao[this.CAO_FRE_1]);
                    } else // Opcao 2
                    {
                        inimigo.setCharset(cao[this.CAO_FRE_2]);
                    // Movimenta inimigo no eixo 'Y'
                    }

                    inimigo.Y += inimigo.VelY;
                }
// Caso tenha se COLIDIDO

            } else {
                inimigo.inX = !inimigo.inX;
                if (inimigo.inX) {
                    if (inimigo.trocaPernas) {
                        inimigo.setCharset(cao[this.CAO_ESQ_1]);
                    } else {
                        inimigo.setCharset(cao[this.CAO_ESQ_2]);
                    }

                    inimigo.X -= inimigo.VelX * 3;
                } else if (!inimigo.inX) {
                    if (inimigo.trocaPernas) {
                        inimigo.setCharset(cao[this.CAO_TRA_1]);
                    } else {
                        inimigo.setCharset(cao[this.CAO_TRA_2]);
                    }

                    inimigo.Y -= inimigo.VelY * 3;
                }

            }
        }
    }

    private void tentaInimigoAMudarEixo(Sprite inimigo) {
        // Faz inimigos andarem
        float perct = (new Random().nextFloat() * 200);

        if (perct < 2) {
            inimigo.inX = !inimigo.inX;
        }

    }

    // Calcula colis�o
    private void calculaColisao() {
    	for(int i  = 0; i < ListaObjetos.size();i++) {
    		Sprite inimigo = ((Sprite)ListaObjetos.elementAt(i));
    		if(Personagem.X+(MAPA.getViewOffsetX() * (-1))+Personagem.Largura >= inimigo.X && Personagem.X+(MAPA.getViewOffsetX() * (-1))-Personagem.Largura <= inimigo.X) {       			
    			if(Personagem.Y+(MAPA.getViewOffsetY() * (-1))+Personagem.Altura >= inimigo.Y && Personagem.Y+(MAPA.getViewOffsetY() * (-1))-(Personagem.Altura/2) <= inimigo.Y) {       			
    				//System.out.println(Personagem.X+(MAPA.getViewOffsetX() * (-1)) + " " + inimigo.X);
    				Personagem.Life--;
    			}           	
    		}

    	}
    }

    // Desenha no buffer
    private void gameRender() {
        if (dbImage == null) { // create the buffer

            dbImage = createImage(PWIDTH, PHEIGHT);
            if (dbImage == null) {
                System.out.println("dbImage is null");
                return;

            } else {
                dbg = (Graphics2D) dbImage.getGraphics();
            }

        }
        // Renderiza o mapa
        this.MAPA.DesenhaSe(dbg);
        // desenha objetos
        for (int i = 0; i < this.imagens.size(); i++) {
            dbg.drawImage(((Imagem) this.imagens.get(i)).image, ((Imagem) this.imagens.get(i)).X + this.MAPA.getViewOffsetX(), ((Imagem) this.imagens.get(i)).Y + this.MAPA.getViewOffsetY(), null);
        }
        // desenha personagem
        this.MAPA.desenhaPersonagem(dbg);        
        // draw game elements
        boolean aux = auxAlguemPerseguindo;
        this.auxAlguemPerseguindo = false;
        for (int i = 0; i <
                ListaObjetos.size(); i++) {
            if (((Sprite) ListaObjetos.elementAt(i)).estaPerseguindo) {
                if (!aux) {
                    this.somLatido.play();
                }
              ((Sprite) ListaObjetos.elementAt(i)).DesenhaSe(dbg, 0,0);
                auxAlguemPerseguindo = true;
            } else {
                ((Sprite) ListaObjetos.elementAt(i)).DesenhaSe(dbg, this.MAPA.getCoordenadaX(), this.MAPA.getCoordenadaY());
            // System.out.println(this.MAPA.getCoordenadaX() + " <*> " + this.MAPA.getCoordenadaY());
            }
            if (!auxAlguemPerseguindo) {
                this.somLatido.close();
                this.auxAlguemPerseguindo = false;
            }
        }

        // GAME OVER
        if (Personagem.Life <= 0) {
            gameOver = true;
            dbg.setColor(new Color(0, 0, 0, 100));
            dbg.fillRect(0, 0, 128, 160);
            dbg.setColor(Color.WHITE);
            dbg.setFont(FonteJogo);
            dbg.drawString("GAME OVER!", 30, 80);
        }

        // 

        
        dbg.setColor(new Color(0, 0, 0, 100));
        dbg.fillRect(0, 0, 128, 20);
        dbg.setColor(new Color(0, 0, 0, 150));
        dbg.fillRect(110, 0, 18, 20);
        dbg.setColor(Color.WHITE);
        dbg.setFont(FonteJogo);
        //dbg.drawString(Integer.toString(FPS), 110, 10);
        dbg.drawString("LEVEL 1", 5, 10);
        dbg.setColor(Color.RED);
        dbg.fillRect(Personagem.Life + 5, 12, 100 - Personagem.Life, 5);
        dbg.setColor(Color.YELLOW);
        dbg.fillRect(5, 12, Personagem.Life, 5);

        if (this.ESCAPE == true) {
            // Retiras um item da pilha
            if (((ItemInventario) this.inventario.get(this.itemSelecionado)).pop() == 1) {
                this.imagens.add(new Imagem(((ItemInventario) this.inventario.get(this.itemSelecionado)).getImage(), (int) Math.round(Personagem.X) + (this.MAPA.getViewOffsetX() * (-1)), (int) Math.round(Personagem.Y) +(Personagem.Altura) + (this.MAPA.getViewOffsetY() * (-1))));
                ESCAPE = false;
                // verifica se colocou chave na porta
                // REFAZER ESTE C�DIGO PARA LER O TEXTO NO BLOCO DA PORTA
                // E NAO USAR COODERNADAS XY
                int x = (int) Math.round(Personagem.X) + (this.MAPA.getViewOffsetX() * (-1));
                int y = (int) Math.round(Personagem.Y) + (this.MAPA.getViewOffsetY() * (-1));
                if(x > 240 && x < 270) {
                	if(y > 123 && y < 138) {
                		if((int)((ItemInventario) this.inventario.get(this.itemSelecionado)).getTipo() == 1) {
                            gameOver = true;
                            dbg.setColor(new Color(0, 0, 0, 100));
                            dbg.fillRect(0, 0, 128, 160);
                            dbg.setColor(Color.WHITE);
                            dbg.setFont(FonteJogo);
                            dbg.drawString("YOU WIN!", 35, 80);
                		}
                	}
                }
            }
        }
        // Pinta inventario       
        this.desenhaInventario();

    // Setar

    }
// Carrega os itens que o personagem pode conseguir para a fase corrente

    private void carregaInventarioDaFase(int fase) {
        if (fase == 0) {
            if (this.inventario != null) {
                this.inventario.clear();
            }

            this.inventario.add(new ItemInventario(carne, ItemInventario.CARNE, 30, 66));
            this.inventario.add(new ItemInventario(chaves, ItemInventario.CHAVE, 81, 42));
        }

    }

    private void desenhaInventario() {
        if (this.inventario == null || this.inventario.isEmpty()) {
            return;
        // Retangulo de base/fundo
        }

        dbg.setColor(new Color(90, 0, 0, 100));
        dbg.fillRect(0, 160 - 12, 160, 12);
        // Calcula constante que sera usada para posicionar os itens na area de inventario
        final int a = Math.round((PWIDTH - 6) / this.inventario.size());
        // Laco que pinta os itens
        for (int i = 0; i <
                this.inventario.size(); i++) {
            dbg.drawImage(((ItemInventario) this.inventario.get(i)).getImage(), 3 + (a * i), 145, null);
            dbg.setColor(Color.WHITE);
            dbg.setFont(new Font("Arial", Font.BOLD, 10));
            dbg.drawString(": " + ((ItemInventario) this.inventario.get(i)).getQtd(), (3 + (a * i)) + ((ItemInventario) this.inventario.get(i)).getImage().getWidth(null) + 1, 157);


        }

        // Desenha o item selecionado do inventario
        dbg.drawImage(((ItemInventario) this.inventario.get(this.itemSelecionado)).getImage(), -1 + PWIDTH - ((ItemInventario) this.inventario.get(this.itemSelecionado)).getImage().getWidth(null), 2, null);


    }
    // end of gameRender()

    // ** TELA */
    // ##########################################################
    @Override
    public void paintComponent(Graphics g) {
        // ????????????????????????????????????????
        super.paintComponent(g);
        if (this.dbImage != null) {
            g.drawImage(this.dbImage, 0, 0, null);
        }

    }

    // ** Trata teclado - Presionado */
    public void TratadorDeTeclado(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if ((keyCode == KeyEvent.VK_Q) || (keyCode == KeyEvent.VK_END) || ((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
            running = false;
        }

        if (PAUSE == true) {
            if (keyCode == KeyEvent.VK_PAUSE) {
                PAUSE = pausado = false;
            }

        } else {
            if (keyCode == KeyEvent.VK_LEFT) {
                ALEFT = true;
            }

            if (keyCode == KeyEvent.VK_RIGHT) {
                ARIGHT = true;
            }

            if (keyCode == KeyEvent.VK_UP) {
                AUP = true;
            }

            if (keyCode == KeyEvent.VK_DOWN) {
                ADOWN = true;
            }

            if (keyCode == KeyEvent.VK_PAUSE && gameOver == false) {
                PAUSE = true;
                pausado =
                        false;
            }
            if (keyCode == KeyEvent.VK_I) {
                LETRA_I = true;
            }
            if (keyCode == KeyEvent.VK_ESCAPE) {
                ESCAPE = true;
            }



        }
        Menu.getInstanciaMenu(this).trataTeclas();
    }

// ** Trata teclado - Solo */
    public void TratadorDeTecladoReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_LEFT) {
            ALEFT = false;
        }

        if (keyCode == KeyEvent.VK_RIGHT) {
            ARIGHT = false;
        }

        if (keyCode == KeyEvent.VK_UP) {
            AUP = false;
        }

        if (keyCode == KeyEvent.VK_DOWN) {
            ADOWN = false;
        }

        if (keyCode == KeyEvent.VK_SPACE) {
            FIRE = false;
        }
        if (keyCode == KeyEvent.VK_I) {
            LETRA_I = false;
        }
        if (keyCode == KeyEvent.VK_ESCAPE) {
            ESCAPE = false;
        }
        Menu.getInstanciaMenu(this).trataTeclas();
    }

// ** Main - Cria instancia do Jogo e seta formatos e propriedades da Tela
// */
    public static void main(String args[]) {
        GamePanel ttPanel = new GamePanel();

        // create a JFrame to hold the timer test JPanel
        JFrame app = new JFrame("The Master Thief");
        app.getContentPane().add(ttPanel, BorderLayout.CENTER);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        app.pack();
        app.setResizable(false);
        app.setVisible(true);
    } // end of main()

    
    class ItemInventario {

        public static final int CARNE = 0;
        public static final int CHAVE = 1;
        private Image image = null;
        private int tipo;
        private int qtd = 100;
        private final int X,  Y;

        public ItemInventario(Image i, int TIPO, int x, int y) {
            image = i;
            tipo = TIPO;
            X = x;
            Y = y;
        }

        public Image getImage() {
            return image;
        }

        public int getTipo() {
            return tipo;
        }

        private int pop() {
            if (qtd > 0) {
                qtd--;
                return 1;
            } else {
                return 0;
            }
        }

        private void push() {
            qtd++;
        }

        public int getQtd() {
            return qtd;
        }
    }

    class Imagem {

        private Image image = null;
        public int X,  Y;

        public Imagem(Image i, int x, int y) {
            image = i;
            X = x;
            Y = y;
        }
    }
}
