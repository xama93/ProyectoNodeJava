package service;

import play.db.Database;
import play.db.Databases;
import play.db.jpa.*;
import org.junit.*;
import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import java.io.FileInputStream;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import models.*;
import services.*;

public class UsuariosServiceTest {

    static Database db;
    static JPAApi jpa;
    JndiDatabaseTester databaseTester;

    @BeforeClass
    static public void initDatabase() {
        db = Databases.inMemoryWith("jndiName", "DefaultDS");
        // Necesario para inicializar el nombre JNDI de la BD
        db.getConnection();
        // Se activa la compatibilidad MySQL en la BD H2
        db.withConnection(connection -> {
            connection.createStatement().execute("SET MODE MySQL;");
        });
        jpa = JPA.createFor("memoryPersistenceUnit");
    }

    @Before
    public void initData() throws Exception {
        databaseTester = new JndiDatabaseTester("DefaultDS");
        IDataSet initialDataSet = new FlatXmlDataSetBuilder().build(new
        FileInputStream("test/resources/usuarios_dataset.xml"));
        databaseTester.setDataSet(initialDataSet);
        databaseTester.onSetup();
    }

    @After
    public void clearData() throws Exception {
        databaseTester.onTearDown();
    }

    @AfterClass
    static public void shutdownDatabase() {
        jpa.shutdown();
        db.shutdown();
    }

    @Test
    public void findUsuarioPorLogin() {
        jpa.withTransaction(() -> {
            Usuario usuario = UsuariosService.findUsuarioPorLogin("juan");
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy");
            try {
                Date diezDiciembre93 = sdf.parse("10-12-1993");
                assertTrue(usuario.login.equals("juan") &&
                           usuario.fechaNacimiento.compareTo(diezDiciembre93) == 0);
            } catch (java.text.ParseException ex) {
                fail("Excepción ParseException");
            }
        });
    }

    @Test
    public void actualizaUsuario() {
        jpa.withTransaction(() -> {
            Usuario usuario = UsuariosService.findUsuario(2);
            usuario.apellidos = "Anabel Pérez";
            UsuariosService.modificaUsuario(usuario);
        });

        jpa.withTransaction(() -> {
            Usuario usuario = UsuariosService.findUsuario(2);
            assertThat(usuario.apellidos, equalTo("Anabel Pérez"));
        });
    }

    @Test
    public void actualizaUsuarioLanzaExcepcionSiLoginYaExiste() {
        jpa.withTransaction(() -> {
            Usuario usuario = UsuariosService.findUsuario(2);

            // Copiamos el objeto usuario para crear un objeto igual
            // pero desconectado de la base de datos. De esta forma,
            // cuando modificamos sus atributos JPA no actualiza la
            // base de datos.

            Usuario desconectado = usuario.copy();
            desconectado.apellidos = "Anabel Pérez";

            // Cambiamos el login por uno ya existente
            desconectado.login = "juan";

            try {
                UsuariosService.modificaUsuario(desconectado);
                fail("No se ha lanzado excepción login ya existe");
            } catch (UsuariosException ex) {
            }
        });
    }
}
