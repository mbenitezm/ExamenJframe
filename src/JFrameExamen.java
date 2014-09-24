/**
 * AppletJuego
 *
 * Personaje para juego previo Examen
 *
 * @author Marcel Benítez 1139855
 * @author Daniela Valdés 813724
 * @version 1.00 2014/09/10
 */
 


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class JFrameExamen extends JFrame implements Runnable, KeyListener {
    
    /* objetos para manejar el buffer del Applet y este no parpadee */
    private Image    imaImagenApplet;   // Imagen a proyectar en Applet	
    private Graphics graGraficaApplet;  // Objeto grafico de la Imagen
    private int iVidas;                  // Entero de conteo de vidas
    private int iScore;                // Entero de conteo de score
    private int iDireccion;            // Direccion de nena
    private int iVelocidad;             // Velocidad de Nena
    private int intY;                   // Controlador de velocidad con vidas
    private int iRandomV;               // Random de velocidad
    private int iRandomCam;                // Generador de numeros random camin
    private int iRandomCorr;               // Generador numeros random corr
    private int iPosX;                   // Posicion en X de objetos
    private int iPosY;                   // Posicion en Y de objetost 
    private int iContVidas;              // Contador para disminuir vidas
    private Personaje perNena;         // Objeto tipo Personaje de nena
    private LinkedList lnkCaminadores;   // Coleccion de caminadores
    private LinkedList lnkCorredores;   // Collecion de corredores
    private boolean bGameOver; // booleana si el juego aun no se acaba
    private Image imgOver;    // Imagen de game over
    private SoundClip aucSon1; // Sonido 1
    private SoundClip aucSon2; // Sonido 2
    private boolean bPausa;         // Si el juego esta pausado
    
    

     
    public JFrameExamen(){
        init();
        start();
    }
    
    /** 
     * init
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se inizializan las variables o se crean los objetos
     * a usarse en el <code>Applet</code> y se definen funcionalidades.
     */
    
    public void init() {        

        this.setSize(800,600);//Set the size of the window

        // El juego empieza
        bGameOver = false;
        
        // El juego inicia sin estar en pausa 
        bPausa = false;
        
        // Genero el numero random para las vidas
        iVidas = (int) (Math.random() * (6 - 3) + 3);
        
        //El contador para disminuir vidas se resetea
        iContVidas = 0;
        
        // Score inicial
        iScore = 0;
        
        // Se crea el sonido para la primera colision
        aucSon1 = new SoundClip ("baboon1.wav");
        
        // Se crea el sonido para la segunda colision
        aucSon2 = new SoundClip("Monkey.wav");
        
        //Se crea la imagen de Nena
        URL urlImagenNena = this.getClass().getResource("nena.gif");
       
        //Se crea a Nena
	perNena = new Personaje(0, 0,
                Toolkit.getDefaultToolkit().getImage(urlImagenNena));
        perNena.setX((getWidth() / 2) - (perNena.getAncho() / 2));
        perNena.setY((getHeight() / 2) - (perNena.getAlto() / 2));
        
        
        // Se crea la coleccion de caminadores y corredores
        lnkCaminadores = new LinkedList();
        lnkCorredores = new LinkedList();
        
        // Se genera un numero random entre 8 y 10 para generar la coleccion
        iRandomCam = (int) (Math.random() * (11 - 8) + 8);
        
        // Se crea la imagen de game over
        URL oURL = this.getClass().getResource ("game over.jpg");
        imgOver = Toolkit.getDefaultToolkit().getImage(oURL);
        
        // Se crea la imagen de los caminadores
        URL urlImagenCam = this.getClass().getResource("alien1Camina.gif");
        
        // Se meten los caminadores a la coleccion
        for (int i = 0; i < iRandomCam; i++){
            iPosX = (((int) (Math.random() * (800))- 800));    
            iPosY =  ((int) (Math.random() * (600)));
            Personaje perCaminador = new Personaje (iPosX, iPosY,
                    Toolkit.getDefaultToolkit().getImage(urlImagenCam));
            lnkCaminadores.add(perCaminador);
        }
        // Se genera un numero random entre 10 y 15 para generar la coleccion
        iRandomCorr = (int) (Math.random() * (16 - 10) + 10);
        
        // Se crea la imagen de los caminadores
        URL urlImagenCorr = this.getClass().getResource("alien2Corre.gif");        

        // Se meten los Corredores a la coleccion
        for (int i = 0; i < iRandomCorr; i++){
            iPosX = ((int) (Math.random() * (800)));    
            iPosY =  (((int) (Math.random() * (600)) - 600));
            Personaje perCorredor = new Personaje (iPosX, iPosY,
                    Toolkit.getDefaultToolkit().getImage(urlImagenCorr));
            lnkCorredores.add(perCorredor);
        }
                
        
        // Se les asigna una velocidad random entre 3 y 5 a los Caminadores
        for (Object lnkCaminadores :lnkCaminadores){
            iRandomV = (int) (Math.random() * (6 - 3) + 3 );
            Personaje perCaminador = (Personaje) lnkCaminadores;
            perCaminador.setVelocidad(iRandomV);
        } 
         
        
        // Se agrega el keylistener
        addKeyListener(this);
    }
    

	
    /** 
     * start
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se crea e inicializa el hilo
     * para la animacion este metodo es llamado despues del init o 
     * cuando el usuario visita otra pagina y luego regresa a la pagina
     * en donde esta este <code>Applet</code>
     * 
     */
    public void start () {
        // Declaras un hilo
        Thread th = new Thread (this);
        // Empieza el hilo
        th.start ();
    }
	
    /** 
     * run
     * 
     * Metodo sobrescrito de la clase <code>Thread</code>.<P>
     * En este metodo se ejecuta el hilo, que contendrá las instrucciones
     * de nuestro juego.
     * 
     */
    public void run () {
        // se realiza el ciclo del juego en este caso nunca termina
        while (!bGameOver) {
            /* mientras dure el juego, se actualizan posiciones de jugadores
               se checa si hubo colisiones para desaparecer jugadores o corregir
               movimientos y se vuelve a pintar todo
            */
            // Si el juego esta en pausa no se checa colisión ni se actualiza
            if (!bPausa){
            actualiza();
            checaColision();
            }
            repaint();
            try	{
                // El thread se duerme.
                Thread.sleep (20);
            }
            catch (InterruptedException iexError)	{
                System.out.println("Hubo un error en el juego " + 
                        iexError.toString());
            }
	}
    }
	
    /** 
     * actualiza
     * 
     * Metodo que actualiza la posicion del objeto elefante 
     * 
     */
    public void actualiza(){
        // Movimiento de nena
        switch(iDireccion) {
            case 1: { //se mueve hacia arriba
                perNena.setY(perNena.getY() - iVelocidad);
                break;    
            }
            case 2: { //se mueve hacia abajo
                perNena.setY(perNena.getY() + iVelocidad);
                break;    
            }
            case 3: { //se mueve hacia izquierda
                perNena.setX(perNena.getX() - iVelocidad);
                break;    
            }
            case 4: { //se mueve hacia derecha
                perNena.setX(perNena.getX() + iVelocidad);
                break;    	
            }
        }
        
        for (Object lnkCaminadores :lnkCaminadores){
            Personaje perCaminador = (Personaje) lnkCaminadores;
            perCaminador.derecha();
        }
        
        for (Object lnkCorredores :lnkCorredores){
            Personaje perCorredor = (Personaje) lnkCorredores;
            intY = (perCorredor.getY() + (6 - iVidas));    
                perCorredor.setY(intY);
        }
       
        // si las vidas se acaban el juego se acaba
        if (iVidas == 0){
           bGameOver = true; 
        }
    }
	
    /**
     * checaColision
     * 
     * Metodo usado para checar la colision del objeto elefante
     * con las orillas del <code>Applet</code>.
     * 
     */
    public void checaColision(){
        // Si choca nena con la parte superior
         if(perNena.getY() < 0){
             perNena.setY(0);
         }
         // Si choca nena con la parte inferior
         if(perNena.getY() + perNena.getAlto() > getHeight()) {
           perNena.setY(getHeight() - perNena.getAlto());
         }
         
         // Si choca nena con la parte derecha
         if(perNena.getX() + perNena.getAncho() > getWidth()) {
             perNena.setX(getWidth() - perNena.getAncho());
         }
         
         // Si choca nena con la parte izquierda
         if (perNena.getX() < 0){
             perNena.setX(0);
         }
         
        // Si colisiona nena con un caminador se aumenta el score por 1
        // y tambien el caminador reaparece random fuera del applet del 
        // lado izquierdo
         
        for (Object lnkCaminadores : lnkCaminadores){
            Personaje perCaminador = (Personaje) lnkCaminadores;   
            if(perNena.colisiona(perCaminador)){             
                iScore++;
                iPosX = (((int) (Math.random() * (800))- 800));    
                iPosY =  ((int) (Math.random() * (600)));
                perCaminador.setX(iPosX);
                perCaminador.setY(iPosY);
                aucSon1.play();
            }
            
            // Si llega al limite derecho se regresa a posicion random 
            // Fuera del applet
            if (perCaminador.getX() + perCaminador.getAncho() > getWidth()){
                iPosX = (((int) (Math.random() * (800))- 800));    
                iPosY =  ((int) (Math.random() * (600)));
                perCaminador.setX(iPosX);
                perCaminador.setY(iPosY); 
            }
            
         // Si choca corredor con la parte superior
         if(perCaminador.getY() < 0){
             perCaminador.setY(0);
         }
         // Si choca corredor con la parte inferior
         if(perCaminador.getY() + perCaminador.getAlto() > getHeight()) {
           perCaminador.setY(getHeight() - perCaminador.getAlto());
         }
         }
        // Si colisiona nena con un corredor 5 veces se disminuye las vidas 
        // y tambien el caminador reaparece random fuera del applet del 
        // lado superior
         
        for (Object lnkCorredores : lnkCorredores){
            Personaje perCorredor = (Personaje) lnkCorredores;   
            if(perNena.colisiona(perCorredor)){             
                iContVidas++;
                if (iContVidas >= 5){
                    iVidas--;
                    iContVidas = 0;
                }
                iPosX = ((int) (Math.random() * (800)));    
                iPosY =  (((int) (Math.random() * (600)) - 600));
                perCorredor.setX(iPosX);
                perCorredor.setY(iPosY);
                aucSon2.play();
            }
            
            // Si llega al limite inferior se regresa a posicion random 
            // Fuera del applet
            if (perCorredor.getY() + perCorredor.getAlto() > getHeight()){
                iPosX = ((int) (Math.random() * (800)));    
                iPosY =  (((int) (Math.random() * (600)) - 600));
                perCorredor.setX(iPosX);
                perCorredor.setY(iPosY); 
            }
         
          // Si choca corredor con la parte derecha
         if(perCorredor.getX() + perCorredor.getAncho() > getWidth()) {
             perCorredor.setX(getWidth() - perCorredor.getAncho());
         }
         
         // Si choca corredor con la parte izquierda
         if (perCorredor.getX() < 0){
             perCorredor.setX(0);
         }
            
         }
    }
	
    /**
     * update
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>,
     * heredado de la clase Container.<P>
     * En este metodo lo que hace es actualizar el contenedor y 
     * define cuando usar ahora el paint
     * @param graGrafico es el <code>objeto grafico</code> usado para dibujar.
     * 
     */
    public void paint (Graphics graGrafico){
        // Inicializan el DoubleBuffer
        if (imaImagenApplet == null){
                imaImagenApplet = createImage (this.getSize().width, 
                        this.getSize().height);
                graGraficaApplet = imaImagenApplet.getGraphics ();
        }
        
        // creo imagen para el background
        URL urlImagenSpace = this.getClass().getResource("espacio.jpg");
        Image imaSpace = Toolkit.getDefaultToolkit().
                getImage(urlImagenSpace);
        
        // Despliega imagen de fondo
        graGraficaApplet.drawImage(imaSpace, 0, 0, 
                getWidth(), getHeight(), this);

        // Actualiza el Foreground.
        graGraficaApplet.setColor (getForeground());
        paint1(graGraficaApplet);

        // Dibuja la imagen actualizada
        graGrafico.drawImage (imaImagenApplet, 0, 0, this);
    }
    
    /**
     * paint
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>,
     * heredado de la clase Container.<P>
     * En este metodo se dibuja la imagen con la posicion actualizada,
     * ademas que cuando la imagen es cargada te despliega una advertencia.
     * @param g es el <code>objeto grafico</code> usado para dibujar.
     * 
     */
    public void paint1(Graphics g) {

        if (!bGameOver){
            // Si las imagenes ya se cargaron
            if (perNena != null & lnkCaminadores != null
                    & lnkCorredores != null){
                // Dibuja las vidas y el score
                //Dibuja la imagen de Nena actualizada
                g.drawImage(perNena.getImagen(), perNena.getX(),
                        perNena.getY(), this);
            
                // Dibuja los caminadores
                for (Object lnkCaminadores :lnkCaminadores){
                    Personaje perCaminador = (Personaje) lnkCaminadores;
                    g.drawImage(perCaminador.getImagen(), perCaminador.getX(),
                            perCaminador.getY(), this);
            }
           
                // Dibujar a los corredores
                for (Object lnkCorredores :lnkCorredores){
                    Personaje perCorredor = (Personaje) lnkCorredores;
                    g.drawImage(perCorredor.getImagen(), perCorredor.getX(),
                            perCorredor.getY(), this);
                }
            }
            // Si no se han cargado se dibuja un mensaje
            else{
                g.drawString("No se cargo la imagen..", 20, 20);
            }
        }
        // Se dibuja el game over cuando el juego termina
        else{
            g.drawImage(imgOver, (getWidth() / 2) - (imgOver.getWidth(this)
                / 2), (getHeight() / 2) - (imgOver.getHeight(this) / 2), 
                this);
        }
        
        // Cambiar el tipo de letra y su tamaño, y dibuja score y vidas
        Font stringFont = new Font( "Times New Roman", Font.PLAIN, 26);
        g.setFont(stringFont);
        g.setColor(Color.RED);
        g.drawString("vidas: " + iVidas + "   Score: " + iScore ,60,60);
        
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        
    }
    
    //Metodo que manda parametros al presionar y soltar una tecla
    @Override
    public void keyReleased(KeyEvent keyEvent) {
         // si presiono W
        if(keyEvent.getKeyCode() == KeyEvent.VK_W){
            iVelocidad = 3;
            iDireccion = 1;
        }
        
        // si presiono S
        else if(keyEvent.getKeyCode() == KeyEvent.VK_S){
             iVelocidad = 3;
             iDireccion = 2;
        }
        
        // si presiono D
        else if(keyEvent.getKeyCode() == KeyEvent.VK_D){
             iVelocidad = 3;
             iDireccion = 4;
        }
        
        //Si presiono A
        else if(keyEvent.getKeyCode() == KeyEvent.VK_A){
             iVelocidad = 3;
             iDireccion = 3;
        }
        
        if(keyEvent.getKeyCode() == KeyEvent.VK_P){
            bPausa = !bPausa;
        }
        
        if(keyEvent.getKeyCode() == KeyEvent.VK_C){
            try {
                leeArchivo();
            } catch (IOException ex) {
                Logger.getLogger(JFrameExamen.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
        if(keyEvent.getKeyCode() == KeyEvent.VK_G){
             try {
                guardaArchivo();
            } catch (IOException ex) {
                Logger.getLogger(JFrameExamen.class.getName())
                        .log(Level.SEVERE, null, ex);
        }
        }
    }
    // Metodo que guarda en un archivo las variables necesarias
    public void guardaArchivo() throws IOException{
        // creo el objeto de salida para grabar en un archivo de texto
    	PrintWriter prwSalida = new PrintWriter(new FileWriter("guardar.txt"));
        // guardo en  linea 1 el score
    	prwSalida.println(iScore);
        // guardo en  linea 2 las vidas
        prwSalida.println(iVidas);
        //Direccion de Nena
        prwSalida.println(iDireccion);
        // Guarda posición de Nena en X en linea 3
        prwSalida.println(perNena.getX());
        // Guarda posiciòn de Nena en Y en linea 4
        prwSalida.println(perNena.getY());
        // Guarda cantidad de caminadores en linea 5
        prwSalida.println(iRandomCam);
        // Guarda cantidad de corredores en linea 6
        prwSalida.println(iRandomCorr);
        // Guarda la velocidad de los caminadores
        prwSalida.println(iRandomV);
        // Guarda posición de caminadores en x y y seguidas
        for (Object lnkCaminadores :lnkCaminadores){
            Personaje perCaminador = (Personaje) lnkCaminadores;
            prwSalida.println(perCaminador.getX());
            prwSalida.println(perCaminador.getY());
        }
        // Guardo en posición x y y seguidas de corredores
        for (Object lnkCorredores :lnkCorredores){
            Personaje perCorredor = (Personaje) lnkCorredores;
            prwSalida.println(perCorredor.getX());
            prwSalida.println(perCorredor.getY());
        }      
        // cierro el archivo
    	prwSalida.close();	
    }
    
    // Metodo que carga de un archivo y guarda en un vector
     public void leeArchivo() throws IOException{
        // defino el objeto de Entrada para tomar datos
    	BufferedReader brwEntrada;
    	try{
            // creo el objeto de entrada a partir de un archivo de texto
            brwEntrada = new BufferedReader(new FileReader("guardar.txt"));
    	} catch (FileNotFoundException e){
                // si marca error es que el archivo no existia entonces lo creo
    		File filPuntos = new File("guardar.txt");
    		PrintWriter prwSalida = new PrintWriter(filPuntos);
                // le pongo datos ficticios o de default
                prwSalida.println("200");
                prwSalida.println("3");
                // lo cierro para que se grabe lo que meti al archivo
    		prwSalida.close();
                // lo vuelvo a abrir porque el objetivo es leer datos
    		brwEntrada = new BufferedReader(new FileReader("guardar.txt"));
    	}
        // Variable para saber la cantidad de caminadores que hay y corredores
        int iCantidadCam;
        int iCantidadCorr;
        int iVelocidadCam;
        // con el archivo abierto leo los datos que estan guardados
        // primero saco el score que esta en la linea 1
    	iScore = Integer.parseInt(brwEntrada.readLine());
        // ahora leo las vidas que esta en la linea 2
    	iVidas = Integer.parseInt(brwEntrada.readLine());
        // Lee la direccion que tenia Nena
        iDireccion = Integer.parseInt(brwEntrada.readLine());;
        // Se asiga la posi ción x de Nena en la linea 3
        perNena.setX(Integer.parseInt(brwEntrada.readLine()));
        // Se asigna la posicion y de NEna en la linea 4
        perNena.setY(Integer.parseInt(brwEntrada.readLine()));
        // Se asigna la cantidad de caminadores que habia
        iCantidadCam = Integer.parseInt(brwEntrada.readLine());
        // Se asigna la cantidad de corredores que hay
        iCantidadCorr = Integer.parseInt(brwEntrada.readLine());
        //Se saca la velocidad de los corredores;
        iVelocidadCam = Integer.parseInt(brwEntrada.readLine());;
        lnkCaminadores.clear();
        lnkCorredores.clear();
        
        // Se crea imagen de los caminadores
        URL urlImagenCam = this.getClass().getResource("alien1Camina.gif");
        // Se meten los caminadores a la coleccion
        for (int i = 0; i < iCantidadCam; i++){
            iPosX = 0;
            iPosY = 0;
            Personaje perCaminador = new Personaje (iPosX, iPosY,
                    Toolkit.getDefaultToolkit().getImage(urlImagenCam));
            lnkCaminadores.add(perCaminador);
        }
        
        // Se crea la imagen de los corredores
        URL urlImagenCorr = this.getClass().getResource("alien2Corre.gif");        
        // Se meten los Corredores a la coleccion
        for (int i = 0; i < iCantidadCorr; i++){
            iPosX = 0;    
            iPosY =  0;
            Personaje perCorredor = new Personaje (iPosX, iPosY,
                    Toolkit.getDefaultToolkit().getImage(urlImagenCorr));
            lnkCorredores.add(perCorredor);
        }
        
        // carga posición de caminadores en x y y seguidas
        for (Object lnkCaminadores :lnkCaminadores){
            Personaje perCaminador = (Personaje) lnkCaminadores;
            perCaminador.setX(Integer.parseInt(brwEntrada.readLine()));
            perCaminador.setY(Integer.parseInt(brwEntrada.readLine()));
        }
        
        // Carga la velocidad anterior de los caminadores
        for (Object lnkCaminadores :lnkCaminadores){
            Personaje perCaminador = (Personaje) lnkCaminadores;
            perCaminador.setVelocidad(iVelocidadCam);
        } 
        
         
        // carga en posición x y y seguidas de corredores
        for (Object lnkCorredores :lnkCorredores){
            Personaje perCorredor = (Personaje) lnkCorredores;
            perCorredor.setX(Integer.parseInt(brwEntrada.readLine()));
            perCorredor.setY(Integer.parseInt(brwEntrada.readLine()));
        }   
  
    	brwEntrada.close();
    }
    
}