package com.example.tresenrayapractica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CODE_PERMISSIONS=1000;
    //botón que se ha pulsado (1 ó 2 jugadores)
    private int numJugadores;
    //array donde guardaremos las casillas del tablero
    private int[] casillas;
    //partida
    private Partida partida;

    SQLiteHelper sql;
    SQLiteDatabase db;

    ContentValues c ;
    ContentValues r ;

    ImageButton sonido;
    MediaPlayer media1;
    MediaPlayer media2;
    MediaPlayer media3;



    int dificultad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sql =  new SQLiteHelper(this);

        sonido = findViewById(R.id.btn_sonido);
        media1= MediaPlayer.create(this,R.raw.elevator);
        media2= MediaPlayer.create(this,R.raw.casilla);
        media3= MediaPlayer.create(this,R.raw.aplauso);
        media1.start();


        //Guardamos cada una de las casillas en el array
        casillas=new int[9];
        casillas[0]=R.id.a1;
        casillas[1]=R.id.a2;
        casillas[2]=R.id.a3;
        casillas[3]=R.id.b1;
        casillas[4]=R.id.b2;
        casillas[5]=R.id.b3;
        casillas[6]=R.id.c1;
        casillas[7]=R.id.c2;
        casillas[8]=R.id.c3;

    }

    int estado = 0;
    public void playpause(View view) {
       if (estado == 0){
           estado = 1;
           media1.pause();
       }else{
           media1.start();
           estado = 0;
       }

    }
    /***
     * Método asociado al evento onClick de los botones de 1 Jugador y 2 Jugadores
     * @param view
     */
    public void inicioJuego(View view) {
        c = new ContentValues();
        r = new ContentValues();
        ImageView imagen;

        //reseteamos el tablero
        //recorremos cada una de los elementos del array y a todos le asignamos la imagen de la casilla en blanco
        for (int casilla : casillas) {
            imagen = findViewById(casilla);
            imagen.setImageResource(R.drawable.casilla);
        }

        //determinamos qué botón se ha pulsado
        numJugadores = 1;
        c.put("jugador1","usu1");
        r.put("usuario","usu1");
        if(view.getId()==R.id.btnDosJugadores){
            numJugadores = 2;
            c.put("jugador2","usu2");
        }else{
            c.put("jugador2","Maquina");
        }

        //comprobamos la dificultad elegida, 0:facil, 1:dificil, 2:extrema
        RadioGroup rgDificultad=findViewById(R.id.radioGroupDificultad);

        int idDif=rgDificultad.getCheckedRadioButtonId();

        dificultad = 0;
       if(idDif==R.id.rbDificil){
            dificultad=1;
            c.put("dificultad","Dificil");
            r.put("dificultad",2);
        }else if(idDif==R.id.rbExtremo){
            dificultad=2;
            c.put("dificultad","Extremo");
            r.put("dificultad",3);
        }else{
            dificultad=0;
            c.put("dificultad","Facil");
            r.put("dificultad",1);
        }

        //comenzamos la partida
        partida=new Partida(dificultad);

        //deshabilitar los botones del tablero mientras dura la partida
        (findViewById(R.id.btnUnJugador)).setEnabled(false);
        (findViewById(R.id.btnDosJugadores)).setEnabled(false);
        (findViewById(R.id.radioGroupDificultad)).setAlpha(0); //lo hacemos transparente

    }

    /***
     * Método asociado al evento onClick de cada una de las casillas de juego
     * @param vista
     */
    public void toqueCasilla(View vista){


        //sólo ejecutaremos el contenido de este método si la partida está comenzada
        if(partida==null){
            return;
        }
        else {
            //comprobamos la casilla en la que se ha pulsado y la guardamos en la variable casillaPulsada
            int casillaPulsada = 0;

            for(int i=0;i<9;i++){
                if (casillas[i]==vista.getId()){ //asignamos a la posición del array el id de la casilla pulsada
                    casillaPulsada=i;
                    break;
                }
            }

            //comprobamos si la casilla está ocupada antes de marcarla
            if(partida.casillaLibre(casillaPulsada)==false){
                return; //nos salimos porque la casilla pulsada está ocupada
            }

            marcarCasilla(casillaPulsada);

            //cambiamos de jugador
            int resultadoJuego=partida.turnoJuego();

            if(resultadoJuego>0){ //o bien hay empate o bien alguien ha ganado
                evaluarFinal(resultadoJuego);
                return; //salimos del método porque alguien ha ganado ya
            }

            //después de marcar la casilla que hemos pulsado hacemos que juege la máquina
            //generamos una casilla al azar
            casillaPulsada=partida.ia();

            //hacemos que si la celda que ha elegido la máquina está ocupada no siga hasta que elija una que
            //esta libre
            while (partida.casillaLibre(casillaPulsada)!=true){
                casillaPulsada=partida.ia();
            }

            //la marcamos
            marcarCasilla(casillaPulsada);

            //volvemos a cambiar el turno
            resultadoJuego=partida.turnoJuego();

            //evaluamos si el juego ha finalizado
            if(resultadoJuego>0){ //o bien hay empate o bien alguien ha ganado
                evaluarFinal(resultadoJuego);
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.item1:
                Intent a1 = new Intent(this, activity_usuarios.class);
                startActivity(a1);
                return true;
            case R.id.item2:
                Intent a2 = new Intent(this, activity_partidas.class);
                startActivity(a2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * Método que comprueba si alguien ha ganado el juego o ha habido empate
     * @param resultadoJuego
     */
    private void evaluarFinal(int resultadoJuego) {
        String mensaje;

        if (resultadoJuego == 1) { //ha ganado el jugador 1
            mensaje = "Jugador 1 ha ganado";
            c.put("resultado", "usu1");
            r.put("puntos", dificultad);
        } else if (resultadoJuego == 2) {//ha ganado el jugador 2
            mensaje = "Jugador 2 ha ganado";
            c.put("resultado", "usu2");
        } else {
            mensaje = "Empate";
            c.put("resultado", "empate");
        }
        Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show();
        media3.start();
        //finalizamos el juego
        partida=null;

        db = sql.getWritableDatabase();
        db.insert("jugadores",null,c);
        db.insert("resultados",null,r);

        //volvemos a habilitar los controles para que se pueda volver a jugar
        (findViewById(R.id.btnUnJugador)).setEnabled(true);
        (findViewById(R.id.btnDosJugadores)).setEnabled(true);
        (findViewById(R.id.radioGroupDificultad)).setAlpha(1); //lo hacemos transparente

        }

    /**
     * Método que dibujará la casilla con un círculo o con un aspa
     * @param casilla
     */
    private void marcarCasilla(int casilla) {
        ImageView imagen;
        imagen = findViewById(casillas[casilla]); //le asignamos el id de la imagen de la casilla correspondiente a la que hay que marcar
        media2.start();
        if (partida.jugador == 1) {
            imagen.setImageResource(R.drawable.circulo); //si el jugador que está marcando es el 1 le asignamos el círculo a la casilla
        } else {
            imagen.setImageResource(R.drawable.aspa); //si el jugadro es el 2 dibujamos un aspa
        }
    }
}