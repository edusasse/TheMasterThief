package com.edusasse.themasterthief;

import java.awt.Graphics2D;
import java.io.FileInputStream;
import java.io.IOException;

import com.alienfactory.javamappy.Map;
import com.alienfactory.javamappy.loader.MapLoader;
import com.alienfactory.javamappy.viewer.MapViewer;
import com.alienfactory.javamappy.viewer.render.JDK12Renderer;
import com.alienfactory.javamappy.viewer.render.Renderer;

public class TileMap {
    // Coordena vizualizacao do Mapa

    private static MapViewer mapViewer;
    // Leitura do Arquivo que Contem o Mapa
    private FileInputStream mapStream;
    // Ponteiro para a Classe gamePanel
    private GamePanel gamePanel;
    // Contem o mapa carregado
    private Map map;
    // Reder do mapa
    private Renderer r;
    // Coordenadas X e Y de deslocamento do Mapa (Offset)
    private int coordenadaX = 100,  coordenadaY = 100;
//    private int coordenadaX = 0,  coordenadaY = 0;
    // Coordenadas de Inicio da Area de Movimentacao do Personagem
    private int areaXini,  areaYini;
    // Tamanho da Area do jogo
    private int tamX,  tamY;
    // Diz se para a interacao corrente o mapa se deslocou
    boolean mapaDeslocou = false;

    //** Construto */
    public TileMap(GamePanel gamePanel) throws IOException {
        // Carrega tela
        this.carregaLayers();
        // Carrega Primeira Fase
        this.carregaFase(0);
        // Game Panel - Controle do Jogo
        this.gamePanel = gamePanel;
        // Passa o percentual da Tela que sera area de jogo
        this.areaDeJogo(75);
    }

    public int getCoordenadaX() {
        return coordenadaX;
    }

    public int getCoordenadaY() {
        return coordenadaY;
    }

    //** Carrega o Arquivo com as Fases do Jogo */
    private void carregaLayers() throws IOException {
        try {
            // Realiza leitura do arquivo que contem as fases do Jogo
            this.mapStream = new FileInputStream(getClass().getClassLoader().getResource("fmp/map.fmp").getFile());           
            // Carega o mapa
            this.map = MapLoader.loadMap(mapStream);
            // Finaliza a Leitura
            this.mapStream.close();
            // Passa o Mapa para o Render
            this.r = new JDK12Renderer(map);
        } catch (IOException ex) {
            throw new IOException(ex);
        }
    }

    //** Carrega a Fase atual */
    private void carregaFase(int fase) throws IOException {
        // Cria a vizualizacao do Mapa segundo a FASE
        this.mapViewer = new MapViewer(map, r, map.getLayers()[fase].getWidthInPixels(), map.getLayers()[fase].getHeightInPixels());
    }

    public boolean isMapaDeslocou() {
        return mapaDeslocou;
    }

    //** Calcula a Area de "movimentacao livre" dentro do jogo */
    private void areaDeJogo(int perct) {
        // Tamanho da Area 'X' = (LarguraDaTela * PercentualDesejado)
        this.tamX = Math.round(new Float(this.gamePanel.PWIDTH * (perct / 100f)));
        // Area de Inicio da "Area X" = {[LarguraDaTela - (LarguraDaTela * PercentualDesejado)]/2}
        this.areaXini = Math.round((this.gamePanel.PWIDTH - this.tamX) / 2f);

        // Tamanho da Area 'Y' = (AlturaDaTela * PercentualDesejado)
        this.tamY = Math.round(new Float(this.gamePanel.PHEIGHT * (perct / 100f)));
        // Area de Inicio da "Area Y" = {[AlturaDaTela - (AlturaDaTela * PercentualDesejado)]/2}
        this.areaXini = Math.round((this.gamePanel.PHEIGHT - this.tamY) / 2f);
    }

    //** Verfica baseado nas coordenadas X ou Y, se o Personagem esta dentro da Area livre de jogo */
    private boolean personagemEstaDentroDaAreaDeJogo(char coordenada) {
        // Para coordenada X
        if (coordenada == 'X') {
            return ( // ( 'Posicao do Personagem' + 'Novo Deslocamento na Coordenada X' - 'Largura do Personagem' ) deve ser MAIOR que posicao 'X Inicial' da Area de Jogo
                    (this.gamePanel.Personagem.X + this.gamePanel.novoX - this.gamePanel.Personagem.Largura / 2d) >= (this.areaXini) &&
                    // ( 'Posicao do Personagem' + 'Novo Deslocamento na Coordenada X' + 'Largura do Personagem' ) deve ser MENOR que O COMPRIMENTO da Area de Jogo
                    (this.gamePanel.Personagem.X + this.gamePanel.novoX + this.gamePanel.Personagem.Largura) <= (this.tamX));
        } else if (coordenada == 'Y') {
            return ((this.gamePanel.Personagem.Y + this.gamePanel.novoY - this.gamePanel.Personagem.Altura * 2) >= (this.areaYini) &&
                    (this.gamePanel.Personagem.Y + this.gamePanel.novoY + this.gamePanel.Personagem.Altura) <= (this.tamY));
        }
        return false;
    }

    //** Calcula a Posicao X relativa no Mapa */
    private void calculaPosicaoMapaX() {
        // Posicao atual do offset
        int posicaoOffSetX = this.mapViewer.getViewOffsetX() * (-1);

        // Se "Nao Estiver na Area de Jogo" && "Posicao atual do OFFSET for 0 - ou seja,
        // o Personagem esta antes da area de movimentacao do mapa - E verifica a regra pelo tamanho
        boolean personagemEstaDentroDaAreaDeJogo = this.personagemEstaDentroDaAreaDeJogo('X');

        if (personagemEstaDentroDaAreaDeJogo || (!personagemEstaDentroDaAreaDeJogo && posicaoOffSetX == 0 && this.gamePanel.Personagem.X <= (this.areaXini + this.gamePanel.Personagem.Largura))) {
            this.gamePanel.Personagem.X += this.gamePanel.novoX;
        } else if (!personagemEstaDentroDaAreaDeJogo) { // Caso nao esteja no quadrado ou
            // seja, estiver no limite do mesmo, deve mover o mapa.
            //this.coordenadaX = Math.round((posicaoOffSetX + this.gamePanel.novoX) * (-1));

            this.coordenadaX = (int) ((posicaoOffSetX + this.gamePanel.novoX) * (-1));
            // Nao deslocar o cao
            this.mapaDeslocou = true;
        }
    }

    //** Calcula a Posicao Y relativa no Mapa */
    private void calculaPosicaoMapaY() {
        // Posicao atual do offset
        int posicaoOffSetY = this.mapViewer.getViewOffsetY() * (-1);

        // Se "Nao Estiver na Area de Jogo" && "Posicao atual do OFFSET for 0 - ou seja,
        // o Personagem esta antes da area de movimentacao do mapa - E verifica a regra pelo tamanho
        boolean personagemEstaDentroDaAreaDeJogo = this.personagemEstaDentroDaAreaDeJogo('Y');
        if (!personagemEstaDentroDaAreaDeJogo && posicaoOffSetY == 0 && this.gamePanel.Personagem.Y <= (this.areaYini + this.gamePanel.Personagem.Largura)) {
            this.gamePanel.Personagem.Y += this.gamePanel.novoY;
        } else if (personagemEstaDentroDaAreaDeJogo) // Caso esteja no quadrado pode se
        // movimentar livremente
        {
            this.gamePanel.Personagem.Y += this.gamePanel.novoY;
        } else if (!personagemEstaDentroDaAreaDeJogo) { // Caso nao esteja no quadrado ou
            // ..seja, estiver no limite do mesmo, deve mover o mapa.

            this.coordenadaY = Math.round((posicaoOffSetY + this.gamePanel.novoY) * (-1));
            // Nao deslocar o cao
            this.mapaDeslocou = true;
        }
    }

    // ** Chama o calculo das coordenadas e desenha o Mapa */
    public void DesenhaSe(Graphics2D dbg) {
        // Estado padrao da variavel
        this.mapaDeslocou = false;
        // Calcula Coordenadas
        this.calculaPosicaoMapaX();
        this.calculaPosicaoMapaY();

//        System.out.println("c_ X;Y: [ " + gamePanel.Personagem.X + " ; " + gamePanel.Personagem.Y + " ]");
//        System.out.println("o_ X;Y: [ " + getViewOffsetX() + " ; " + getViewOffsetY() + " ]");

        // Verifica se ha colissao para a Posicao relativa do personagem
        if (map.getLayers()[0].isCollisionAt(
                (int) (Math.round(this.gamePanel.Personagem.X - this.coordenadaX) + this.gamePanel.Personagem.Largura / 2),
                (int) (Math.round(this.gamePanel.Personagem.Y - this.coordenadaY)) + this.gamePanel.Personagem.Altura)) {
            // Caso haja colisao as coordenadas X & Y retornam ao valor antes da movimentacao
            this.gamePanel.novoX = this.gamePanel.novoX * (-1);
            this.gamePanel.novoY = this.gamePanel.novoY * (-1);
            // Recalcula as coordenadas e retorna sem redesenhar
            this.calculaPosicaoMapaX();
            this.calculaPosicaoMapaY();
            return;
        } else {
            // Caso nao haja colisao, as novas coo
            // Passa a posicao do mapa para renderizar com as devidas coordenadas
            this.mapViewer.setViewOffsetX(this.coordenadaX);
            this.mapViewer.setViewOffsetY(this.coordenadaY);
            // Desenha mapa
            this.mapViewer.draw(dbg, false);
//            // Desenha personagem, as coordenadas X & Y sao passadas como zero porque ja foram setadas anteriormente
//            this.gamePanel.Personagem.DesenhaSe(dbg, 0, 0);
        }
        // Zera novos X e Y
        this.gamePanel.novoX = 0;
        this.gamePanel.novoY = 0;
    }

    public void desenhaPersonagem(Graphics2D dbg) {
        // Desenha personagem, as coordenadas X & Y sao passadas como zero porque ja foram setadas anteriormente
        this.gamePanel.Personagem.DesenhaSe(dbg, 0, 0);  	
    }
    
    public boolean isColisionAt(int x, int y) {
        return map.getLayers()[0].isCollisionAt(x, y);
    }

    public int getViewOffsetX() {
        return this.mapViewer.getViewOffsetX();
    }

    public int getViewOffsetY() {
        return this.mapViewer.getViewOffsetY();
    }
    
    
}
