package com.edusasse.themasterthief;
import java.awt.Graphics2D;
import java.awt.Image;

public class Sprite extends Objeto {
    
    public Image Charset = null;
    
    double X,Y, oldX, oldY;
    float VelX,VelY;
    int FrameTime;
    long Tempo;
    int Anim;
    int Aceleracao;
    int Character;
    int Frame;
    int charx,chary;
    boolean EmCharset;
    int Altura,Largura;
    int NFrames,NAnimations;
    public boolean inX = true;
    public boolean trocaPernas = false;
    // Verdadeiro se o personagem estiver perseguindo na interacao corrente
    public boolean estaPerseguindo = false;

    
    
    public void setCharset(Image Charset) {
        this.Charset = Charset;
    }
    
    int Tipo;
    
    int Life = 100;
    
    public Sprite(Image charset,int x,int y,int charac,boolean emcharset,int largura,int altura,int nframes,int nanimations){
        X = x;
        Y = y;
        
        VelX = 0;
        VelY = 0;
        
        FrameTime = 100;
        
        Tempo = 0;
        
        Anim = 0;
        
        Character = charac;
        
        charx = (Character%nframes)*(nframes*largura);
        chary = (Character/nanimations)*(nanimations*altura);
        
        Frame = 0;
        
        Charset = charset;
        
        EmCharset = emcharset;
        
        Altura = altura;
        Largura = largura;
        
        NFrames = nframes;
        NAnimations = nanimations;
        
        Tipo = 0;
        
    }
    
    public void DesenhaSe(Graphics2D dbg, int coordenadaX, int coordenadaY) {
        if(EmCharset==true){
            dbg.drawImage(Charset,(int)X-(coordenadaX),(int)Y-(coordenadaY),(int)X+Largura,(int)Y+Altura,charx+(Frame*Largura),chary+(Anim*Altura),charx+(Frame*Largura)+Largura,chary+(Anim*Altura)+Altura,null);
        }else{            
            dbg.drawImage(Charset,(int)this.X-((coordenadaX)*(-1)), (int)this.Y-((coordenadaY)*(-1)), null);
        }
    }
    
    
    public void SimulaSe(long diftime) {
        Tempo += diftime;
        Frame = ((int)(Tempo/FrameTime))%NFrames;
    }
    
}