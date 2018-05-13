package Pokemon;

import com.sun.glass.events.KeyEvent;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;

/**
 * PokeDex basada en la version del Pokemon Heart Gold para DS.
 *
 * @author Alejandro Luna Gomez
 */
public class VentanaPokedex extends javax.swing.JFrame {

    /* ------------------------------ Variables ----------------------------- */
    BufferedImage plantilla = null;
    BufferedImage huellas = null;
    AudioInputStream audioIn = null;
    private int contador = 0;
    int total_pokemons = 0;

    // Variables de conexion a la base de datos
    private Statement estado;
    private ResultSet resultadoConsulta;
    private Connection conexion;

    //Variables para almacenar datos
    HashMap<String, Pokemon> listaPokemons = new HashMap();
    HashMap<String, BufferedImage> listaTipos = new HashMap();

    /**
     * Devuelve la imagen del pokemon en la posicion indicada con las medidas
     * solicitadas.
     *
     * @param posicion - int
     * @param ancho - int
     * @param alto - int
     * @return - ImageIcon
     */
    private ImageIcon devuelveElPokemonQueEstaEnLaPosicion(int posicion, int ancho, int alto) {
	int columna = posicion / 31;
	int fila = posicion % 31;
	return (new ImageIcon(plantilla.getSubimage(fila * 96, columna * 96, 96, 96)
		.getScaledInstance(ancho, alto, Image.SCALE_DEFAULT)));
    }

    /**
     * Reproduce el grito del Pokemon actual
     */
    private void reproduceGrito() {
	try {
	    String sonido = "/gritos/" + (contador + 1) + ".wav";
	    Clip clip = AudioSystem.getClip();
	    clip.open(AudioSystem.getAudioInputStream(getClass().getResource(sonido)));
	    clip.loop(0);
	} catch (Exception e) {
	    e.getMessage();
	}
    }

    private void upButtonPressed() {
	int sum = 10;
	while (sum > 0) {
	    if (contador + sum > total_pokemons - 1) {
		if (contador + 1 <= total_pokemons - 1) {
		    contador++;
		    sum--;
		} else {
		    contador = 0;
		    sum--;
		}
	    } else {
		contador += sum;
		sum = 0;
	    }
	}
	try {
	    cruceta.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/crucetaup.png"))));
	} catch (IOException ex) {
	}
	escribeDatos();
    }

    private void downButtonPressed() {
	int res = 10;
	while (res > 0) {
	    if (contador - res < 0) {
		if (contador - 1 >= 0) {
		    contador--;
		    res--;
		} else {
		    contador = total_pokemons - 1;
		    res--;
		}
	    } else {
		contador -= res;
		res = 0;
	    }
	}
	try {
	    cruceta.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/crucetadown.png"))));
	} catch (IOException ex) {
	}
	escribeDatos();
    }

    private void rightButtonPressed() {
	contador++;
	if (contador > total_pokemons - 1) {
	    contador = 0;
	}
	try {
	    cruceta.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/crucetaright.png"))));
	} catch (IOException ex) {
	}
	escribeDatos();
    }

    private void leftButtonPressed() {
	contador--;
	if (contador < 0) {
	    contador = total_pokemons - 1;
	}
	try {
	    cruceta.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/crucetaleft.png"))));
	} catch (IOException ex) {
	}
	escribeDatos();
    }

    private void buscaPokemon() {
	String palabraBusqueda = busLabel.getText();
	try {
	    palabraBusqueda = palabraBusqueda.toLowerCase();
	    palabraBusqueda = palabraBusqueda.substring(0, 1).toUpperCase() + palabraBusqueda.substring(1);
	    Class.forName("com.mysql.jdbc.Driver");
	    conexion = DriverManager.getConnection("jdbc:mysql://127.0.0.1/pokemones", "root", "root");
	    estado = conexion.createStatement();
	    resultadoConsulta = estado.executeQuery("SELECT buscaNombre('" + palabraBusqueda + "')");
	    if (resultadoConsulta.next() && resultadoConsulta.getInt(1) > 0) {
		contador = (resultadoConsulta.getInt(1) - 1);
		busError.setText(null);
	    } else {
		busError.setText(palabraBusqueda + " no es un pokemon");
	    }
	} catch (Exception e) {
	    e.getMessage();
	    busError.setText("Introduce un nombre");

	}
	escribeDatos();
    }

    /**
     * Cambia los valores de las etiquetas en consecuencia al pokemon
     * seleccionado.
     */
    private void escribeDatos() {
	Pokemon p = listaPokemons.get(String.valueOf(contador + 1));
	if (p != null) {
	    ventanaDetalles.setTitle(String.format("%03d", p.id) + " - " + p.nombre.toUpperCase());
	    ventanaBusqueda.setTitle("Busqueda");
	    /* ---------------------- Ventana Principal --------------------- */
	    imgLabel.setIcon(devuelveElPokemonQueEstaEnLaPosicion(contador, 96, 96));
	    idLabel.setText(String.format("%03d", p.id));
	    nameLabel1.setText(p.nombre.toUpperCase());
	    alturaLabel.setText(p.altura + " m");
	    pesoLabel.setText(p.peso + " kg");
	    especieLabel.setText("Pokemon " + p.especie);
	    descLabel.setText("<html><p align='center'>" + p.descripcion + "</p></html>");
	    /* ---------------------- Ventana Detalles ---------------------- */
	    detImg.setIcon(devuelveElPokemonQueEstaEnLaPosicion(contador, 96, 96));
	    detIDLabel.setText(String.format("%03d", p.id));
	    detNombreLabel.setText(p.nombre.toUpperCase());
	    detAlturaLabel.setText(p.altura + " m");
	    detPesoLabel.setText(p.peso + " kg");
	    detEspecieLabel.setText("Pokemon " + p.especie);
	    detDescripcionLabel.setText("<html><p align='center'>" + p.descripcion + "</p></html>");
	    detMov1.setText(p.movimiento1);
	    detMov2.setText(p.movimiento2);
	    detMov3.setText(p.movimiento3);
	    detMov4.setText(p.movimiento4);
	    detHabitatLabel.setText(p.habitat);
	    detHabilidadLabel.setText(p.habilidad);
	    /* ---------------------------- Otros --------------------------- */
	    // Pre - Evolucion (Mixto - Ventana Principal / Ventana Detalles)
	    try {
		Pokemon prePokemon = listaPokemons.get(String.valueOf(p.preEvolucion));
		preEvBoxCont.setIcon(devuelveElPokemonQueEstaEnLaPosicion(prePokemon.id - 1, 70, 70));
		//detPreEvImg.setIcon(devuelveElPokemonQueEstaEnLaPosicion(prePokemon.id - 1, 70, 70));
		detPreEvImg.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/iconos/" + p.preEvolucion + ".png"))));
		preEvName.setText(prePokemon.nombre.toUpperCase());
		detPreEvLabel.setText(prePokemon.nombre.toUpperCase());
	    } catch (Exception e) {
		preEvBoxCont.setIcon(null);
		detPreEvImg.setIcon(null);
		preEvName.setText(null);
		detPreEvLabel.setText(null);
	    }
	    // Pos - Evolucion (Mixto - Ventana Principal / Ventana Detalles)
	    try {
		Pokemon posPokemon = listaPokemons.get(String.valueOf(p.posEvolucion));
		posEvBoxCont.setIcon(devuelveElPokemonQueEstaEnLaPosicion(posPokemon.id - 1, 70, 70));
		//detPosEvImg.setIcon(devuelveElPokemonQueEstaEnLaPosicion(posPokemon.id - 1, 70, 70));
		detPosEvImg.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/iconos/" + p.posEvolucion + ".png"))));
		posEvName.setText(posPokemon.nombre.toUpperCase());
		detPosEvLabel.setText(posPokemon.nombre.toUpperCase());
	    } catch (Exception e) {
		posEvBoxCont.setIcon(null);
		detPosEvImg.setIcon(null);
		posEvName.setText(null);
		detPosEvLabel.setText(null);
	    }

	    // Tipo 1 (Mixto - Ventana Principal / Ventana Detalles)
	    if (listaTipos.containsKey(p.tipo1)) {
		tipo1Label.setIcon(new ImageIcon(listaTipos.get(p.tipo1)));
		detTipo1.setIcon(new ImageIcon(listaTipos.get(p.tipo1)));
	    } else {
		try {
		    BufferedImage t1 = ImageIO.read(getClass().getResource("/tipos/" + p.tipo1.toLowerCase() + ".gif"));
		    listaTipos.put(p.tipo1, t1);
		    tipo1Label.setIcon(new ImageIcon(t1));
		    detTipo1.setIcon(new ImageIcon(t1));
		} catch (Exception e) {
		    tipo1Label.setIcon(null);
		    detTipo1.setIcon(null);
		}
	    }
	    // Tipo 2 (Mixto - Ventana Principal / Ventana Detalles)
	    if (listaTipos.containsKey(p.tipo2)) {
		tipo2Label.setIcon(new ImageIcon(listaTipos.get(p.tipo2)));
		detTipo2.setIcon(new ImageIcon(listaTipos.get(p.tipo2)));
	    } else {
		try {
		    BufferedImage t2 = ImageIO.read(getClass().getResource("/tipos/" + p.tipo2.toLowerCase() + ".gif"));
		    listaTipos.put(p.tipo2, t2);
		    tipo2Label.setIcon(new ImageIcon(t2));
		    detTipo2.setIcon(new ImageIcon(t2));
		} catch (Exception e) {
		    tipo2Label.setIcon(null);
		    detTipo2.setIcon(null);
		}
	    }
	    // Huella (Mixto - Ventana Principal / Ventana Detalles)
	    try {
		footprintBoxLabel.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/huellas/" + (contador + 1) + ".png"))));
		detHuellaImg.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/huellas/" + (contador + 1) + ".png"))));
	    } catch (Exception e) {
		footprintBoxLabel.setIcon(null);
		detHuellaImg.setIcon(null);
	    }
	} else { // Si no encuentra el pokemon
	    detNombreLabel.setText("NO HAY DATOS");
	}

    }

    public VentanaPokedex() {
	initComponents();
	try {
	    plantilla = ImageIO.read(getClass().getResource("/template/black-white.png"));

	} catch (IOException e) {
	}

	//conexion a la base de datos//////////////////
	try {
	    Class.forName("com.mysql.jdbc.Driver");
	    conexion = DriverManager.getConnection("jdbc:mysql://127.0.0.1/pokemones", "root", "root");
	    estado = conexion.createStatement();
	    resultadoConsulta = estado.executeQuery("SELECT * FROM pokemones.pokemon");
	    //cargo el resultado de la query en mi hashmap
	    while (resultadoConsulta.next()) {
		Pokemon p = new Pokemon();
		p.id = resultadoConsulta.getInt(1);
		p.nombre = resultadoConsulta.getString(2);
		p.altura = resultadoConsulta.getString(3);
		p.peso = resultadoConsulta.getString(4);
		p.especie = resultadoConsulta.getString(5);
		p.habitat = resultadoConsulta.getString(6);
		p.tipo1 = resultadoConsulta.getString(7);
		p.tipo2 = resultadoConsulta.getString(8);
		p.habilidad = resultadoConsulta.getString(9);
		p.movimiento1 = resultadoConsulta.getString(10);
		p.movimiento2 = resultadoConsulta.getString(11);
		p.movimiento3 = resultadoConsulta.getString(12);
		p.movimiento4 = resultadoConsulta.getString(13);
		p.preEvolucion = resultadoConsulta.getInt(14);
		p.posEvolucion = resultadoConsulta.getInt(15);
		p.descripcion = resultadoConsulta.getString(16);
		listaPokemons.put(resultadoConsulta.getString(1), p);
	    }
	} catch (Exception e) {
	    e.getMessage();
	}
	total_pokemons = listaPokemons.size();
	try {
	    ventanaDetalles.setIconImage(new ImageIcon(ImageIO.read(getClass().getResource("/template/logo.png"))).getImage());
	    ventanaBusqueda.setIconImage(new ImageIcon(ImageIO.read(getClass().getResource("/template/logo.png"))).getImage());
	} catch (Exception e) {
	}
	escribeDatos();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ventanaDetalles = new javax.swing.JFrame();
        detNombreLabel = new javax.swing.JLabel();
        detNombre = new javax.swing.JLabel();
        detIDLabel = new javax.swing.JLabel();
        detID = new javax.swing.JLabel();
        detImg = new javax.swing.JLabel();
        detAlturaLabel = new javax.swing.JLabel();
        detAltura = new javax.swing.JLabel();
        detPesoLabel = new javax.swing.JLabel();
        detPeso = new javax.swing.JLabel();
        detEspecieLabel = new javax.swing.JLabel();
        detEspecie = new javax.swing.JLabel();
        detHabitatLabel = new javax.swing.JLabel();
        detHabitat = new javax.swing.JLabel();
        detTipo1 = new javax.swing.JLabel();
        detTipo2 = new javax.swing.JLabel();
        detHabilidadLabel = new javax.swing.JLabel();
        detHabilidad = new javax.swing.JLabel();
        detMovimientos = new javax.swing.JLabel();
        detMov1 = new javax.swing.JLabel();
        detMov2 = new javax.swing.JLabel();
        detMov3 = new javax.swing.JLabel();
        detMov4 = new javax.swing.JLabel();
        detPreEv = new javax.swing.JLabel();
        detPreEvLabel = new javax.swing.JLabel();
        detPreEvImg = new javax.swing.JLabel();
        detPosEv = new javax.swing.JLabel();
        detPosEvLabel = new javax.swing.JLabel();
        detPosEvImg = new javax.swing.JLabel();
        detDescripcionLabel = new javax.swing.JLabel();
        detDescripcion = new javax.swing.JLabel();
        detHuella = new javax.swing.JLabel();
        detHuellaImg = new javax.swing.JLabel();
        detBaseColor = new javax.swing.JLabel();
        ventanaBusqueda = new javax.swing.JFrame();
        busError = new javax.swing.JLabel();
        busLabel = new javax.swing.JTextField();
        busButton = new javax.swing.JButton();
        busBaseColor = new javax.swing.JLabel();
        aButton = new javax.swing.JLabel();
        bButton = new javax.swing.JLabel();
        xButton = new javax.swing.JLabel();
        yButton = new javax.swing.JLabel();
        upButton = new javax.swing.JLabel();
        rightButton = new javax.swing.JLabel();
        leftButton = new javax.swing.JLabel();
        downButton = new javax.swing.JLabel();
        plusTenLabel = new javax.swing.JLabel();
        plusOneLabel = new javax.swing.JLabel();
        minusOneLabel = new javax.swing.JLabel();
        minusTenLabel = new javax.swing.JLabel();
        cruceta = new javax.swing.JLabel();
        buttonB = new javax.swing.JLabel();
        buttonA = new javax.swing.JLabel();
        buttonX = new javax.swing.JLabel();
        buttonY = new javax.swing.JLabel();
        preEvolBoxButton = new javax.swing.JLabel();
        posEvLabel = new javax.swing.JLabel();
        posEvName = new javax.swing.JLabel();
        posEvBoxCont = new javax.swing.JLabel();
        posEvolBox = new javax.swing.JLabel();
        preEvLabel = new javax.swing.JLabel();
        preEvName = new javax.swing.JLabel();
        preEvBoxCont = new javax.swing.JLabel();
        preEvolBox = new javax.swing.JLabel();
        topBar = new javax.swing.JLabel();
        bottomBar = new javax.swing.JLabel();
        leftBar = new javax.swing.JLabel();
        tipo1Label = new javax.swing.JLabel();
        tipo2Label = new javax.swing.JLabel();
        descLabel = new javax.swing.JLabel();
        especieLabel = new javax.swing.JLabel();
        pesoLabel = new javax.swing.JLabel();
        alturaLabel = new javax.swing.JLabel();
        pesLabel1 = new javax.swing.JLabel();
        altLabel = new javax.swing.JLabel();
        idLabel = new javax.swing.JLabel();
        nameLabel1 = new javax.swing.JLabel();
        imgLabel = new javax.swing.JLabel();
        headerBox = new javax.swing.JLabel();
        sizesBox = new javax.swing.JLabel();
        textBox = new javax.swing.JLabel();
        footprintBoxLabel = new javax.swing.JLabel();
        footprintBox = new javax.swing.JLabel();
        background = new javax.swing.JLabel();
        logoPokemon = new javax.swing.JLabel();
        Firma = new javax.swing.JLabel();
        colabLabel = new javax.swing.JLabel();
        baseColor = new javax.swing.JLabel();
        posEvolBoxButton = new javax.swing.JLabel();

        ventanaDetalles.setResizable(false);
        ventanaDetalles.setSize(new java.awt.Dimension(500, 395));
        ventanaDetalles.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        detNombreLabel.setFont(new java.awt.Font("Pokemon Classic", 1, 8)); // NOI18N
        detNombreLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        detNombreLabel.setText("Agumon");
        ventanaDetalles.getContentPane().add(detNombreLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, 130, 20));

        detNombre.setFont(new java.awt.Font("Pokemon Classic", 1, 11)); // NOI18N
        detNombre.setText("Nombre:");
        ventanaDetalles.getContentPane().add(detNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, -1, -1));

        detIDLabel.setFont(new java.awt.Font("Pokemon Classic", 0, 11)); // NOI18N
        detIDLabel.setText("000");
        ventanaDetalles.getContentPane().add(detIDLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, -1, -1));

        detID.setFont(new java.awt.Font("Pokemon Classic", 0, 11)); // NOI18N
        detID.setText("ID");
        ventanaDetalles.getContentPane().add(detID, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        detImg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        detImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/black-white.png"))); // NOI18N
        detImg.setPreferredSize(new java.awt.Dimension(96, 96));
        ventanaDetalles.getContentPane().add(detImg, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 35, -1, -1));

        detAlturaLabel.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        detAlturaLabel.setText("1,65 m");
        ventanaDetalles.getContentPane().add(detAlturaLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, 170, 20));

        detAltura.setFont(new java.awt.Font("Pokemon Classic", 1, 11)); // NOI18N
        detAltura.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        detAltura.setText("Altura:");
        ventanaDetalles.getContentPane().add(detAltura, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 100, -1));

        detPesoLabel.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        detPesoLabel.setText("70 kg");
        ventanaDetalles.getContentPane().add(detPesoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 70, 170, 20));

        detPeso.setFont(new java.awt.Font("Pokemon Classic", 1, 11)); // NOI18N
        detPeso.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        detPeso.setText("Peso:");
        ventanaDetalles.getContentPane().add(detPeso, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 100, -1));

        detEspecieLabel.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        detEspecieLabel.setText("Homo Sapiens Sapiens");
        ventanaDetalles.getContentPane().add(detEspecieLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, 170, 20));

        detEspecie.setFont(new java.awt.Font("Pokemon Classic", 1, 11)); // NOI18N
        detEspecie.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        detEspecie.setText("Especie:");
        ventanaDetalles.getContentPane().add(detEspecie, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 100, -1));

        detHabitatLabel.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        detHabitatLabel.setText("AulaAndroid");
        ventanaDetalles.getContentPane().add(detHabitatLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 130, 170, 20));

        detHabitat.setFont(new java.awt.Font("Pokemon Classic", 1, 11)); // NOI18N
        detHabitat.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        detHabitat.setText("Habitat:");
        ventanaDetalles.getContentPane().add(detHabitat, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 100, -1));

        detTipo1.setFont(new java.awt.Font("Pokemon Classic", 0, 11)); // NOI18N
        ventanaDetalles.getContentPane().add(detTipo1, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 140, -1, -1));

        detTipo2.setFont(new java.awt.Font("Pokemon Classic", 0, 11)); // NOI18N
        ventanaDetalles.getContentPane().add(detTipo2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 140, -1, -1));

        detHabilidadLabel.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        detHabilidadLabel.setText("Programar");
        ventanaDetalles.getContentPane().add(detHabilidadLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 160, 170, 20));

        detHabilidad.setFont(new java.awt.Font("Pokemon Classic", 1, 11)); // NOI18N
        detHabilidad.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        detHabilidad.setText("Habilidad:");
        ventanaDetalles.getContentPane().add(detHabilidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 100, -1));

        detMovimientos.setFont(new java.awt.Font("Pokemon Classic", 1, 11)); // NOI18N
        detMovimientos.setText("Movimientos:");
        ventanaDetalles.getContentPane().add(detMovimientos, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 200, -1, -1));

        detMov1.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        detMov1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        detMov1.setText("Movimiento Sismico");
        detMov1.setPreferredSize(new java.awt.Dimension(140, 13));
        ventanaDetalles.getContentPane().add(detMov1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, -1, -1));

        detMov2.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        detMov2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        detMov2.setText("Patada Salto Alta");
        detMov2.setPreferredSize(new java.awt.Dimension(140, 13));
        ventanaDetalles.getContentPane().add(detMov2, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 230, -1, -1));

        detMov3.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        detMov3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        detMov3.setText("Picotazo Venenoso");
        detMov3.setPreferredSize(new java.awt.Dimension(140, 13));
        ventanaDetalles.getContentPane().add(detMov3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 140, -1));

        detMov4.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        detMov4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        detMov4.setText("Movimiento Espejo");
        detMov4.setPreferredSize(new java.awt.Dimension(140, 13));
        ventanaDetalles.getContentPane().add(detMov4, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 250, -1, -1));

        detPreEv.setFont(new java.awt.Font("Pokemon Classic", 1, 10)); // NOI18N
        detPreEv.setText("Pre-Evolucion");
        ventanaDetalles.getContentPane().add(detPreEv, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 170, -1, -1));

        detPreEvLabel.setFont(new java.awt.Font("Pokemon Classic", 1, 8)); // NOI18N
        detPreEvLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        detPreEvLabel.setText("Agumon");
        ventanaDetalles.getContentPane().add(detPreEvLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 190, 130, -1));

        detPreEvImg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        detPreEvImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/1.png"))); // NOI18N
        ventanaDetalles.getContentPane().add(detPreEvImg, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 205, -1, -1));

        detPosEv.setFont(new java.awt.Font("Pokemon Classic", 1, 10)); // NOI18N
        detPosEv.setText("Pos-Evolucion");
        ventanaDetalles.getContentPane().add(detPosEv, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 260, -1, -1));

        detPosEvLabel.setFont(new java.awt.Font("Pokemon Classic", 1, 8)); // NOI18N
        detPosEvLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        detPosEvLabel.setText("Agumon");
        ventanaDetalles.getContentPane().add(detPosEvLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 280, 130, -1));

        detPosEvImg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        detPosEvImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/1.png"))); // NOI18N
        ventanaDetalles.getContentPane().add(detPosEvImg, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 295, -1, -1));

        detDescripcionLabel.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        detDescripcionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        detDescripcionLabel.setText("Esto es una descripción.");
        detDescripcionLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        ventanaDetalles.getContentPane().add(detDescripcionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 310, 250, 50));

        detDescripcion.setFont(new java.awt.Font("Pokemon Classic", 1, 11)); // NOI18N
        detDescripcion.setText("Descripción:");
        ventanaDetalles.getContentPane().add(detDescripcion, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, -1, -1));

        detHuella.setFont(new java.awt.Font("Pokemon Classic", 1, 10)); // NOI18N
        detHuella.setText("Huella:");
        ventanaDetalles.getContentPane().add(detHuella, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 50, -1, -1));

        detHuellaImg.setFont(new java.awt.Font("Pokemon Classic", 0, 11)); // NOI18N
        detHuellaImg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ventanaDetalles.getContentPane().add(detHuellaImg, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 70, 50, 50));

        detBaseColor.setBackground(new java.awt.Color(255, 51, 51));
        detBaseColor.setForeground(new java.awt.Color(255, 51, 51));
        detBaseColor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/baseColor.png"))); // NOI18N
        detBaseColor.setPreferredSize(new java.awt.Dimension(573, 445));
        ventanaDetalles.getContentPane().add(detBaseColor, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 380));

        ventanaBusqueda.setMinimumSize(new java.awt.Dimension(300, 110));
        ventanaBusqueda.setResizable(false);
        ventanaBusqueda.setSize(new java.awt.Dimension(300, 120));
        ventanaBusqueda.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        busError.setForeground(new java.awt.Color(255, 51, 51));
        ventanaBusqueda.getContentPane().add(busError, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 170, 20));

        busLabel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                busLabelKeyPressed(evt);
            }
        });
        ventanaBusqueda.getContentPane().add(busLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 170, 30));

        busButton.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        busButton.setText("Buscar");
        busButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                busButtonMousePressed(evt);
            }
        });
        ventanaBusqueda.getContentPane().add(busButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 30, -1, 30));

        busBaseColor.setBackground(new java.awt.Color(255, 51, 51));
        busBaseColor.setForeground(new java.awt.Color(255, 51, 51));
        busBaseColor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/baseColor.png"))); // NOI18N
        busBaseColor.setPreferredSize(new java.awt.Dimension(573, 445));
        ventanaBusqueda.getContentPane().add(busBaseColor, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 300, 110));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 51, 51));
        setMaximumSize(new java.awt.Dimension(256, 384));
        setMinimumSize(new java.awt.Dimension(256, 384));
        setResizable(false);
        setSize(new java.awt.Dimension(573, 445));
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        aButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/abutton.png"))); // NOI18N
        aButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                aButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                aButtonMouseReleased(evt);
            }
        });
        getContentPane().add(aButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(503, 274, -1, -1));

        bButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/bbutton.png"))); // NOI18N
        bButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                bButtonMouseReleased(evt);
            }
        });
        getContentPane().add(bButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(469, 308, -1, -1));

        xButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/xbutton.png"))); // NOI18N
        xButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                xButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                xButtonMouseReleased(evt);
            }
        });
        getContentPane().add(xButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(469, 240, -1, -1));

        yButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/ybutton.png"))); // NOI18N
        yButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                yButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                yButtonMouseReleased(evt);
            }
        });
        getContentPane().add(yButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(435, 274, -1, -1));

        upButton.setPreferredSize(new java.awt.Dimension(34, 34));
        upButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                upButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                upButtonMouseReleased(evt);
            }
        });
        getContentPane().add(upButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(334, 240, -1, -1));

        rightButton.setPreferredSize(new java.awt.Dimension(34, 34));
        rightButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                rightButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                rightButtonMouseReleased(evt);
            }
        });
        getContentPane().add(rightButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(368, 274, -1, -1));

        leftButton.setPreferredSize(new java.awt.Dimension(34, 34));
        leftButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                leftButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                leftButtonMouseReleased(evt);
            }
        });
        getContentPane().add(leftButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 274, -1, -1));

        downButton.setPreferredSize(new java.awt.Dimension(34, 34));
        downButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                downButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                downButtonMouseReleased(evt);
            }
        });
        getContentPane().add(downButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(334, 308, -1, -1));

        plusTenLabel.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        plusTenLabel.setText("+10");
        getContentPane().add(plusTenLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(339, 226, -1, -1));

        plusOneLabel.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        plusOneLabel.setText("+1");
        getContentPane().add(plusOneLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(405, 283, -1, -1));

        minusOneLabel.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        minusOneLabel.setText("-1");
        getContentPane().add(minusOneLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(285, 283, -1, -1));

        minusTenLabel.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        minusTenLabel.setText("-10");
        getContentPane().add(minusTenLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(339, 340, -1, -1));

        cruceta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/cruceta.png"))); // NOI18N
        getContentPane().add(cruceta, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 240, -1, -1));

        buttonB.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        buttonB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/buttonb.png"))); // NOI18N
        buttonB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buttonBMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                buttonBMouseReleased(evt);
            }
        });
        getContentPane().add(buttonB, new org.netbeans.lib.awtextra.AbsoluteConstraints(214, 366, -1, -1));

        buttonA.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        buttonA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/buttona.png"))); // NOI18N
        buttonA.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buttonAMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                buttonAMouseReleased(evt);
            }
        });
        getContentPane().add(buttonA, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 366, -1, -1));

        buttonX.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        buttonX.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/buttonx.png"))); // NOI18N
        buttonX.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buttonXMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                buttonXMouseReleased(evt);
            }
        });
        getContentPane().add(buttonX, new org.netbeans.lib.awtextra.AbsoluteConstraints(86, 366, -1, -1));

        buttonY.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        buttonY.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/buttony.png"))); // NOI18N
        buttonY.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buttonYMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                buttonYMouseReleased(evt);
            }
        });
        getContentPane().add(buttonY, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 366, -1, -1));

        preEvolBoxButton.setPreferredSize(new java.awt.Dimension(100, 106));
        getContentPane().add(preEvolBoxButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 230, -1, -1));

        posEvLabel.setFont(new java.awt.Font("Pokemon Classic", 0, 6)); // NOI18N
        posEvLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        posEvLabel.setText("Pos-Evolución");
        getContentPane().add(posEvLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(161, 235, 100, -1));

        posEvName.setFont(new java.awt.Font("Pokemon Classic", 1, 6)); // NOI18N
        posEvName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        posEvName.setText("Pre-Evolución");
        getContentPane().add(posEvName, new org.netbeans.lib.awtextra.AbsoluteConstraints(161, 245, 100, -1));

        posEvBoxCont.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/black-white.png"))); // NOI18N
        posEvBoxCont.setPreferredSize(new java.awt.Dimension(70, 70));
        getContentPane().add(posEvBoxCont, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 259, -1, -1));

        posEvolBox.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/evolbox.png"))); // NOI18N
        getContentPane().add(posEvolBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 230, -1, -1));

        preEvLabel.setFont(new java.awt.Font("Pokemon Classic", 0, 6)); // NOI18N
        preEvLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        preEvLabel.setText("Pre-Evolución");
        getContentPane().add(preEvLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(51, 235, 100, -1));

        preEvName.setFont(new java.awt.Font("Pokemon Classic", 1, 6)); // NOI18N
        preEvName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        preEvName.setText("Pre-Evolución");
        getContentPane().add(preEvName, new org.netbeans.lib.awtextra.AbsoluteConstraints(51, 245, 100, -1));

        preEvBoxCont.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/black-white.png"))); // NOI18N
        preEvBoxCont.setPreferredSize(new java.awt.Dimension(70, 70));
        getContentPane().add(preEvBoxCont, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 259, -1, -1));

        preEvolBox.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/evolbox.png"))); // NOI18N
        getContentPane().add(preEvolBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 230, -1, -1));

        topBar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/topbar.png"))); // NOI18N
        getContentPane().add(topBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 256, 18));

        bottomBar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/buttonbar.png"))); // NOI18N
        getContentPane().add(bottomBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 364, 256, 37));

        leftBar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/leftbar.png"))); // NOI18N
        leftBar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                leftBarMousePressed(evt);
            }
        });
        getContentPane().add(leftBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 212, -1, -1));

        tipo1Label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tipos/acero.gif"))); // NOI18N
        getContentPane().add(tipo1Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(164, 83, -1, -1));

        tipo2Label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tipos/acero.gif"))); // NOI18N
        getContentPane().add(tipo2Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(216, 83, -1, -1));

        descLabel.setFont(new java.awt.Font("Pokemon Classic", 0, 7)); // NOI18N
        descLabel.setForeground(new java.awt.Color(0, 0, 0));
        descLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        descLabel.setText("Descripcion");
        descLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        getContentPane().add(descLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 155, 220, 40));

        especieLabel.setFont(new java.awt.Font("Pokemon Classic", 1, 6)); // NOI18N
        especieLabel.setForeground(new java.awt.Color(0, 0, 0));
        especieLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        especieLabel.setText("Pokemon Especie");
        especieLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        getContentPane().add(especieLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 63, 140, 11));

        pesoLabel.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        pesoLabel.setForeground(new java.awt.Color(0, 0, 0));
        pesoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pesoLabel.setText("kg");
        pesoLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        getContentPane().add(pesoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(205, 126, 60, 11));

        alturaLabel.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        alturaLabel.setForeground(new java.awt.Color(0, 0, 0));
        alturaLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        alturaLabel.setText("m");
        alturaLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        getContentPane().add(alturaLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(205, 109, 60, 11));

        pesLabel1.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        pesLabel1.setForeground(new java.awt.Color(0, 0, 0));
        pesLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        pesLabel1.setText("PESO");
        pesLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        getContentPane().add(pesLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(167, 126, 40, 11));

        altLabel.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        altLabel.setForeground(new java.awt.Color(0, 0, 0));
        altLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        altLabel.setText("ALT.");
        altLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        getContentPane().add(altLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(167, 109, 40, 11));

        idLabel.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        idLabel.setForeground(new java.awt.Color(255, 255, 255));
        idLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        idLabel.setText("Num.");
        idLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        getContentPane().add(idLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(142, 45, 30, 11));

        nameLabel1.setFont(new java.awt.Font("Pokemon Classic", 1, 8)); // NOI18N
        nameLabel1.setForeground(new java.awt.Color(255, 255, 255));
        nameLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        nameLabel1.setText("Pokemon");
        nameLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        getContentPane().add(nameLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(173, 45, 90, 11));

        imgLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imgLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/black-white.png"))); // NOI18N
        getContentPane().add(imgLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 45, 96, 96));

        headerBox.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/headerbox.png"))); // NOI18N
        getContentPane().add(headerBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(123, 43, 146, 34));

        sizesBox.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/sizesbox.png"))); // NOI18N
        getContentPane().add(sizesBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(163, 107, 106, 34));

        textBox.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/textbox.png"))); // NOI18N
        getContentPane().add(textBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(26, 150, 244, 52));

        footprintBoxLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        footprintBoxLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/footprintbox.png"))); // NOI18N
        getContentPane().add(footprintBoxLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(123, 83, 34, 34));

        footprintBox.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/footprintbox.png"))); // NOI18N
        getContentPane().add(footprintBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(123, 83, 34, 34));

        background.setBackground(new java.awt.Color(255, 255, 255));
        background.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/background.png"))); // NOI18N
        getContentPane().add(background, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 256, 384));

        logoPokemon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/pokemonheartgold.png"))); // NOI18N
        logoPokemon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                logoPokemonMousePressed(evt);
            }
        });
        getContentPane().add(logoPokemon, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 20, -1, -1));

        Firma.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        Firma.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        Firma.setText("Por Alejandro Luna (XelleX)");
        Firma.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                FirmaMousePressed(evt);
            }
        });
        getContentPane().add(Firma, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 430, 190, -1));

        colabLabel.setFont(new java.awt.Font("Pokemon Classic", 0, 8)); // NOI18N
        colabLabel.setText("BBDD por Marco Girbau");
        getContentPane().add(colabLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 430, -1, -1));

        baseColor.setBackground(new java.awt.Color(255, 51, 51));
        baseColor.setForeground(new java.awt.Color(255, 51, 51));
        baseColor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/template/baseColor.png"))); // NOI18N
        baseColor.setPreferredSize(new java.awt.Dimension(573, 445));
        getContentPane().add(baseColor, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        posEvolBoxButton.setPreferredSize(new java.awt.Dimension(100, 106));
        getContentPane().add(posEvolBoxButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 230, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonYMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonYMousePressed
	if (evt.getButton() == 1) {
	    try {
		buttonY.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttonypress.png"))));
	    } catch (Exception e) {

	    }
	    ventanaBusqueda.setVisible(true);
	}

    }//GEN-LAST:event_buttonYMousePressed

    private void buttonYMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonYMouseReleased
	if (evt.getButton() == 1) {
	    try {
		buttonY.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttony.png"))));
	    } catch (Exception e) {
	    }
	}
    }//GEN-LAST:event_buttonYMouseReleased

    private void buttonXMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonXMousePressed
	if (evt.getButton() == 1) {
	    try {
		buttonX.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttonxpress.png"))));
	    } catch (Exception e) {
	    }
	    reproduceGrito();
	}
    }//GEN-LAST:event_buttonXMousePressed

    private void buttonXMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonXMouseReleased
	if (evt.getButton() == 1) {
	    try {
		buttonX.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttonx.png"))));
	    } catch (Exception e) {
	    }
	}
    }//GEN-LAST:event_buttonXMouseReleased

    private void buttonAMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonAMousePressed
	if (evt.getButton() == 1) {
	    try {
		buttonA.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttonapress.png"))));
	    } catch (Exception e) {
	    }
	    ventanaDetalles.setVisible(true);
	}
    }//GEN-LAST:event_buttonAMousePressed

    private void buttonAMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonAMouseReleased
	if (evt.getButton() == 1) {
	    try {
		buttonA.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttona.png"))));
	    } catch (Exception e) {
	    }
	}
    }//GEN-LAST:event_buttonAMouseReleased

    private void buttonBMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonBMousePressed
	if (evt.getButton() == 1) {
	    try {
		buttonB.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttonbpress.png"))));
	    } catch (Exception e) {
	    }
	    System.exit(0);
	}
    }//GEN-LAST:event_buttonBMousePressed

    private void buttonBMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonBMouseReleased
	if (evt.getButton() == 1) {
	    try {
		buttonB.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttonb.png"))));
	    } catch (Exception e) {
	    }
	}
    }//GEN-LAST:event_buttonBMouseReleased

    private void rightButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rightButtonMousePressed
	if (evt.getButton() == 1) {
	    rightButtonPressed();
	}
    }//GEN-LAST:event_rightButtonMousePressed

    private void leftButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_leftButtonMousePressed
	if (evt.getButton() == 1) {
	    leftButtonPressed();
	}
    }//GEN-LAST:event_leftButtonMousePressed

    private void upButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_upButtonMousePressed
	if (evt.getButton() == 1) {
	    upButtonPressed();
	}
    }//GEN-LAST:event_upButtonMousePressed

    private void downButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_downButtonMousePressed
	if (evt.getButton() == 1) {
	    downButtonPressed();
	}
    }//GEN-LAST:event_downButtonMousePressed

    private void rightButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rightButtonMouseReleased
	if (evt.getButton() == 1) {
	    try {
		cruceta.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/cruceta.png"))));
	    } catch (IOException ex) {
	    }
	}
    }//GEN-LAST:event_rightButtonMouseReleased

    private void leftButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_leftButtonMouseReleased
	if (evt.getButton() == 1) {
	    try {
		cruceta.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/cruceta.png"))));
	    } catch (IOException ex) {
	    }
	}
    }//GEN-LAST:event_leftButtonMouseReleased

    private void upButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_upButtonMouseReleased
	if (evt.getButton() == 1) {
	    try {
		cruceta.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/cruceta.png"))));
	    } catch (IOException ex) {
	    }
	}
    }//GEN-LAST:event_upButtonMouseReleased

    private void downButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_downButtonMouseReleased
	if (evt.getButton() == 1) {
	    try {
		cruceta.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/cruceta.png"))));
	    } catch (IOException ex) {
	    }
	}
    }//GEN-LAST:event_downButtonMouseReleased

    private void aButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_aButtonMousePressed
	if (evt.getButton() == 1) {
	    try {
		aButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/abuttonpress.png"))));
		buttonA.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttonapress.png"))));
	    } catch (IOException ex) {
	    }
	    ventanaDetalles.setVisible(true);
	}
    }//GEN-LAST:event_aButtonMousePressed

    private void xButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_xButtonMousePressed
	if (evt.getButton() == 1) {
	    try {
		xButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/xbuttonpress.png"))));
		buttonX.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttonxpress.png"))));
	    } catch (IOException ex) {
	    }
	    reproduceGrito();
	}
    }//GEN-LAST:event_xButtonMousePressed

    private void yButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_yButtonMousePressed
	if (evt.getButton() == 1) {
	    try {
		yButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/ybuttonpress.png"))));
		buttonY.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttonypress.png"))));
	    } catch (IOException ex) {
	    }
	    ventanaBusqueda.setVisible(true);
	}
    }//GEN-LAST:event_yButtonMousePressed

    private void bButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bButtonMousePressed
	if (evt.getButton() == 1) {
	    try {
		bButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/bbuttonpress.png"))));
		buttonB.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttonbpress.png"))));
	    } catch (IOException ex) {
	    }
	    System.exit(0);
	}
    }//GEN-LAST:event_bButtonMousePressed

    private void bButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bButtonMouseReleased
	if (evt.getButton() == 1) {
	    try {
		bButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/bbutton.png"))));
		buttonB.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttonb.png"))));
	    } catch (IOException ex) {
	    }
	}
    }//GEN-LAST:event_bButtonMouseReleased

    private void aButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_aButtonMouseReleased
	if (evt.getButton() == 1) {
	    try {
		aButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/abutton.png"))));
		buttonA.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttona.png"))));
	    } catch (IOException ex) {
	    }
	}
    }//GEN-LAST:event_aButtonMouseReleased

    private void xButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_xButtonMouseReleased
	if (evt.getButton() == 1) {
	    try {
		xButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/xbutton.png"))));
		buttonX.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttonx.png"))));
	    } catch (IOException ex) {
	    }
	}
    }//GEN-LAST:event_xButtonMouseReleased

    private void yButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_yButtonMouseReleased
	if (evt.getButton() == 1) {
	    try {
		yButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/ybutton.png"))));
		buttonY.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttony.png"))));
	    } catch (IOException ex) {
	    }
	}
    }//GEN-LAST:event_yButtonMouseReleased

    private void FirmaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_FirmaMousePressed
	if (evt.getButton() == 1) {
	    try {
		if (Desktop.isDesktopSupported()) {
		    Desktop.getDesktop().browse(new URI("https://xellex.es"));
		}
	    } catch (Exception e) {
	    }
	}
    }//GEN-LAST:event_FirmaMousePressed

    private void logoPokemonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoPokemonMousePressed
	if (evt.getButton() == 1) {
	    try {
		if (Desktop.isDesktopSupported()) {
		    Desktop.getDesktop().browse(new URI("http://es.pokemon.wikia.com/wiki/Pok%C3%A9mon_Oro_HeartGold_y_Pok%C3%A9mon_Plata_SoulSilver"));
		}
	    } catch (Exception e) {
	    }
	}
    }//GEN-LAST:event_logoPokemonMousePressed

    private void busButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_busButtonMousePressed
	if (evt.getButton() == 1) {
	    buscaPokemon();
	}
    }//GEN-LAST:event_busButtonMousePressed

    private void leftBarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_leftBarMousePressed
	if (evt.getButton() == 1) {
	    ventanaDetalles.setVisible(true);
	}
    }//GEN-LAST:event_leftBarMousePressed

    private void busLabelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_busLabelKeyPressed
	if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
	    buscaPokemon();
	}
    }//GEN-LAST:event_busLabelKeyPressed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
	switch (evt.getKeyCode()) {
	    case KeyEvent.VK_A:
		try {
		    aButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/abuttonpress.png"))));
		    buttonA.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttonapress.png"))));
		} catch (IOException ex) {
		}
		ventanaDetalles.setVisible(true);
		break;
	    case KeyEvent.VK_B:
		try {
		    bButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/bbuttonpress.png"))));
		    buttonB.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttonbpress.png"))));
		} catch (IOException ex) {
		}
		System.exit(0);
		break;
	    case KeyEvent.VK_X:
		try {
		    xButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/xbuttonpress.png"))));
		    buttonX.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttonxpress.png"))));
		} catch (IOException ex) {
		}
		reproduceGrito();
		break;
	    case KeyEvent.VK_Y:
		try {
		    yButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/ybuttonpress.png"))));
		    buttonY.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttonypress.png"))));
		} catch (IOException ex) {
		}
		ventanaBusqueda.setVisible(true);
		break;
	    case KeyEvent.VK_UP:
		upButtonPressed();
		break;
	    case KeyEvent.VK_DOWN:
		downButtonPressed();
		break;
	    case KeyEvent.VK_RIGHT:
		rightButtonPressed();
		break;
	    case KeyEvent.VK_LEFT:
		leftButtonPressed();
		break;
	}
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
	try {
	    aButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/abutton.png"))));
	    buttonA.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttona.png"))));
	    bButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/bbutton.png"))));
	    buttonB.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttonb.png"))));
	    xButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/xbutton.png"))));
	    buttonX.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttonx.png"))));
	    yButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/ybutton.png"))));
	    buttonY.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/buttony.png"))));
	    cruceta.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/template/cruceta.png"))));
	} catch (IOException ex) {
	}
    }//GEN-LAST:event_formKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
	/* Set the Nimbus look and feel */
	//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
	/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
	 */
	try {
	    for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
		if ("Nimbus".equals(info.getName())) {
		    javax.swing.UIManager.setLookAndFeel(info.getClassName());
		    break;

		}
	    }
	} catch (ClassNotFoundException ex) {
	    java.util.logging.Logger.getLogger(VentanaPokedex.class
		    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

	} catch (InstantiationException ex) {
	    java.util.logging.Logger.getLogger(VentanaPokedex.class
		    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

	} catch (IllegalAccessException ex) {
	    java.util.logging.Logger.getLogger(VentanaPokedex.class
		    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

	} catch (javax.swing.UnsupportedLookAndFeelException ex) {
	    java.util.logging.Logger.getLogger(VentanaPokedex.class
		    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
	}
	//</editor-fold>

	/* Create and display the form */
	java.awt.EventQueue.invokeLater(new Runnable() {
	    public void run() {
		VentanaPokedex ventana = new VentanaPokedex();
		ventana.setVisible(true);
		ventana.setTitle("PokeDex Version Heart Gold - Por Alejandro Luna");
		try {
		    ventana.setIconImage(new ImageIcon(ImageIO.read(getClass().getResource("/template/logo.png"))).getImage());
		} catch (Exception e) {
		}
	    }
	});
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Firma;
    private javax.swing.JLabel aButton;
    private javax.swing.JLabel altLabel;
    private javax.swing.JLabel alturaLabel;
    private javax.swing.JLabel bButton;
    private javax.swing.JLabel background;
    private javax.swing.JLabel baseColor;
    private javax.swing.JLabel bottomBar;
    private javax.swing.JLabel busBaseColor;
    private javax.swing.JButton busButton;
    private javax.swing.JLabel busError;
    private javax.swing.JTextField busLabel;
    private javax.swing.JLabel buttonA;
    private javax.swing.JLabel buttonB;
    private javax.swing.JLabel buttonX;
    private javax.swing.JLabel buttonY;
    private javax.swing.JLabel colabLabel;
    private javax.swing.JLabel cruceta;
    private javax.swing.JLabel descLabel;
    private javax.swing.JLabel detAltura;
    private javax.swing.JLabel detAlturaLabel;
    private javax.swing.JLabel detBaseColor;
    private javax.swing.JLabel detDescripcion;
    private javax.swing.JLabel detDescripcionLabel;
    private javax.swing.JLabel detEspecie;
    private javax.swing.JLabel detEspecieLabel;
    private javax.swing.JLabel detHabilidad;
    private javax.swing.JLabel detHabilidadLabel;
    private javax.swing.JLabel detHabitat;
    private javax.swing.JLabel detHabitatLabel;
    private javax.swing.JLabel detHuella;
    private javax.swing.JLabel detHuellaImg;
    private javax.swing.JLabel detID;
    private javax.swing.JLabel detIDLabel;
    private javax.swing.JLabel detImg;
    private javax.swing.JLabel detMov1;
    private javax.swing.JLabel detMov2;
    private javax.swing.JLabel detMov3;
    private javax.swing.JLabel detMov4;
    private javax.swing.JLabel detMovimientos;
    private javax.swing.JLabel detNombre;
    private javax.swing.JLabel detNombreLabel;
    private javax.swing.JLabel detPeso;
    private javax.swing.JLabel detPesoLabel;
    private javax.swing.JLabel detPosEv;
    private javax.swing.JLabel detPosEvImg;
    private javax.swing.JLabel detPosEvLabel;
    private javax.swing.JLabel detPreEv;
    private javax.swing.JLabel detPreEvImg;
    private javax.swing.JLabel detPreEvLabel;
    private javax.swing.JLabel detTipo1;
    private javax.swing.JLabel detTipo2;
    private javax.swing.JLabel downButton;
    private javax.swing.JLabel especieLabel;
    private javax.swing.JLabel footprintBox;
    private javax.swing.JLabel footprintBoxLabel;
    private javax.swing.JLabel headerBox;
    private javax.swing.JLabel idLabel;
    private javax.swing.JLabel imgLabel;
    private javax.swing.JLabel leftBar;
    private javax.swing.JLabel leftButton;
    private javax.swing.JLabel logoPokemon;
    private javax.swing.JLabel minusOneLabel;
    private javax.swing.JLabel minusTenLabel;
    private javax.swing.JLabel nameLabel1;
    private javax.swing.JLabel pesLabel1;
    private javax.swing.JLabel pesoLabel;
    private javax.swing.JLabel plusOneLabel;
    private javax.swing.JLabel plusTenLabel;
    private javax.swing.JLabel posEvBoxCont;
    private javax.swing.JLabel posEvLabel;
    private javax.swing.JLabel posEvName;
    private javax.swing.JLabel posEvolBox;
    private javax.swing.JLabel posEvolBoxButton;
    private javax.swing.JLabel preEvBoxCont;
    private javax.swing.JLabel preEvLabel;
    private javax.swing.JLabel preEvName;
    private javax.swing.JLabel preEvolBox;
    private javax.swing.JLabel preEvolBoxButton;
    private javax.swing.JLabel rightButton;
    private javax.swing.JLabel sizesBox;
    private javax.swing.JLabel textBox;
    private javax.swing.JLabel tipo1Label;
    private javax.swing.JLabel tipo2Label;
    private javax.swing.JLabel topBar;
    private javax.swing.JLabel upButton;
    private javax.swing.JFrame ventanaBusqueda;
    private javax.swing.JFrame ventanaDetalles;
    private javax.swing.JLabel xButton;
    private javax.swing.JLabel yButton;
    // End of variables declaration//GEN-END:variables
}
