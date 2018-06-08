package crudandroid.erivan.com.crudandroidsql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText textoTarefa;
    private Button botaoAdicionar;
    private ListView listaTarefas;
    private SQLiteDatabase bancoDeDados;

    //arrays
    private ArrayAdapter<String> itensAdaptado;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            //recuperar os componentes
            textoTarefa = (EditText) findViewById(R.id.textoId);
            botaoAdicionar = (Button) findViewById(R.id.botaoAdicionarId);


            //lista
            listaTarefas = (ListView) findViewById(R.id.listViewId);

            //banco de dados
            bancoDeDados = openOrCreateDatabase("bdtarefas", MODE_PRIVATE, null);

            //tabela do bd chamada tarefas
            bancoDeDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas(id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa varchar) ");

            //clicou no botão
            botaoAdicionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   //recupera o que foi digitado
                    String textoInformado = textoTarefa.getText().toString();
                    salvarTarefa(textoInformado);
                }
            });

            listaTarefas.setLongClickable(true);
            listaTarefas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    //metodo remover tarefas
                    removerTarefa( ids.get( position ) );
                    return true;
                }
            });

            //recupera as tarefas na lista
            recuperarTarefas();



        }catch (Exception e){
            e.printStackTrace();
        }
}

private void salvarTarefa(String texto){

  try{

      //se texto estiver bazio exibe a mensagem
      if(texto.equals("")){
          Toast.makeText(MainActivity.this, "Informa uma tarefa", Toast.LENGTH_SHORT).show();
      }else{
          //salvando tarefa no banco de dados
          bancoDeDados.execSQL("INSERT INTO tarefas (tarefa) VALUES ('" + texto + "')");
          Toast.makeText(MainActivity.this, "Tarefa salva com sucesso!", Toast.LENGTH_SHORT).show();
          //salvou ja recupera a tarefa
          recuperarTarefas();
          //limpa o campo da tarefa informada
          textoTarefa.setText("");
      }

  }catch (Exception e){
      e.printStackTrace();
  }
}

private void recuperarTarefas(){
    try{
        //recuperar as tarefas
        //ordena por forma decrescente os ids
        Cursor cursor = bancoDeDados.rawQuery("SELECT * FROM tarefas ORDER BY id DESC", null);

        //recuperar os ids das colunas
        int indiceColunaId = cursor.getColumnIndex("id");
        int indiceColunaTarefa = cursor.getColumnIndex("tarefa");


        //Criar adaptador
        itens = new ArrayList<String>();
        ids = new ArrayList<Integer>();
        itensAdaptado = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_2,
                android.R.id.text2,
                itens){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text2);
                text.setTextColor(Color.BLACK);
                return view;

            }
        };
                listaTarefas.setAdapter(itensAdaptado);

        //listar as tarefas
        cursor.moveToFirst();
        while (cursor != null) {
            //exibe no logcat na opção info digitando resultado
            Log.i("Resultado - ", "Id Tarefa: " + cursor.getString(indiceColunaId) + " Tarefa: " + cursor.getString(indiceColunaTarefa));
            //adiciona o elemento no arraylist itens e id
            itens.add(cursor.getString(indiceColunaTarefa));
            ids.add(Integer.parseInt(cursor.getString(indiceColunaId)));
//tendo tarefa ele vai exibindo
            cursor.moveToNext();
        }


    }catch (Exception e ){
             e.printStackTrace();
    }
}

    private void removerTarefa(Integer id){
        try{

            bancoDeDados.execSQL("DELETE FROM tarefas WHERE id="+id);
            recuperarTarefas();
            Toast.makeText(MainActivity.this, "Tarefa removida com sucesso!", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
