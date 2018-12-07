package br.edu.unoesc.webmob.offtrail.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;
import java.util.stream.Collectors;

import br.edu.unoesc.webmob.offtrail.R;
import br.edu.unoesc.webmob.offtrail.adapter.TrilheiroAdapter;
import br.edu.unoesc.webmob.offtrail.helper.DatabaseHelper;
import br.edu.unoesc.webmob.offtrail.model.Grupo_Trilheiro;
import br.edu.unoesc.webmob.offtrail.model.Trilheiro;

@EViewGroup(R.layout.lista_trilheiros)
public class TrilheiroItemView extends LinearLayout {

    @ViewById
    TextView txtNome;

    @ViewById
    TextView txtMoto;

    @ViewById
    ImageView imvFoto;

    //variável global
    Trilheiro trilheiro;

    @Bean
    DatabaseHelper dh;

    @Bean
    TrilheiroAdapter ta;
    public TrilheiroItemView(Context context) {
        super(context);
    }

    @Click(R.id.imvEditar)
    public void editar() {
        //intent para chamar tela de cadastro

        Intent itCadastrarTrilheiro = new Intent(getContext(), TrilheiroActivity_.class);
        itCadastrarTrilheiro.putExtra("trilheiro", trilheiro);
        getContext().startActivity(itCadastrarTrilheiro);

        Toast.makeText(getContext(), "Editar: " + trilheiro.getNome(), Toast.LENGTH_SHORT).show();
    }

    @Click(R.id.imvExcluir)
    public void excluir() {

        AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
        dialogo.setTitle("Exclusão");
        dialogo.setMessage("Deseja realmente excluir? - " + trilheiro.getNome());
        dialogo.setCancelable(false);
        dialogo.setNegativeButton("Não", null);
        dialogo.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    //remover grupo trilheiro
                    for (Grupo_Trilheiro x : dh.getGrupoTrilheiroDao().queryForAll()) {
                        if (x.getTrilheiro().getCodigo().equals(trilheiro.getCodigo())) {
                            dh.getGrupoTrilheiroDao().delete(x);
                        }
                    }
                    //remover trilheiro
                    dh.getTrilheiroDao().delete(trilheiro);
                    ta.ordenarLista();
                    ta.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        dialogo.show();
//        Toast.makeText(getContext(), "Excluir: " + trilheiro.getNome(), Toast.LENGTH_SHORT).show();
    }

    public void bind(Trilheiro t) {
        trilheiro = t;
        txtNome.setText(t.getNome());
        txtMoto.setText(t.getMoto().getDescricao() + " - " + t.getMoto().getCilindrada());
        imvFoto.setImageBitmap(BitmapFactory.decodeByteArray(t.getFoto(), 0, t.getFoto().length));
    }
}