package dao.jdbc;

import dao.AluguelDAO;
import dao.ClienteDAO;
import entidades.Aluguel;
import entidades.Cliente;
import entidades.Filme;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AluguelDAOImpl implements AluguelDAO {
    @Override
    public void insert(Connection conn, Aluguel aluguel) throws Exception {
        PreparedStatement myStmt = conn.prepareStatement("insert into en_aluguel (id_aluguel, id_cliente, data_aluguel, valor ) values (?, ?, ?, ?)");

        Integer idAluguel = this.getNextId(conn);

        myStmt.setInt(1, idAluguel);
        myStmt.setInt(2, aluguel.getCliente().getIdCliente());
        myStmt.setDate(3, new java.sql.Date(aluguel.getDataAluguel().getTime()));
        myStmt.setFloat(4, aluguel.getValor());
        myStmt.execute();

        aluguel.setIdAluguel(idAluguel);

        PreparedStatement myStmt1 = conn.prepareStatement("insert into re_aluguel_filme (id_aluguel, id_filme) values (?, ?)");
        for (Filme filme : aluguel.getFilmes()) {
            myStmt1.setInt(1, idAluguel);
            myStmt1.setInt(2, filme.getIdFilme());
            myStmt1.execute();
            myStmt1.clearParameters();
        }

        conn.commit();
    }

    @Override
    public Integer getNextId(Connection conn) throws Exception {
        PreparedStatement myStmt = conn.prepareStatement("select nextval('seq_en_aluguel')");
        ResultSet rs = myStmt.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    @Override
    public void edit(Connection conn, Aluguel aluguel) throws Exception {

        if (aluguel.getIdAluguel() == null) {
            throw new Exception("id do aluguel é obrigatorio");
        }

        PreparedStatement myStmt = conn.prepareStatement("update en_aluguel set id_cliente = ?, data_aluguel = ?, valor = ? where id_aluguel = ?;");

        myStmt.setInt(1, aluguel.getCliente().getIdCliente());
        myStmt.setDate(2, new java.sql.Date(aluguel.getDataAluguel().getTime()));
        myStmt.setFloat(3, aluguel.getValor());
        myStmt.setInt(4, aluguel.getIdAluguel());
        myStmt.execute();

        // deletar os ren aluguel com o id do aluguel tabela de associacao
        PreparedStatement myStmt2 = conn.prepareStatement("delete from re_aluguel_filme where re_aluguel_filme.id_aluguel = ?");

        myStmt2.setInt(1, aluguel.getIdAluguel());

        myStmt2.execute();

        // inserir os novos filmes na tabela ren aluguel tabela associacao
        PreparedStatement myStmt3 = conn.prepareStatement("insert into re_aluguel_filme (id_aluguel, id_filme) values (?, ?)");

        for (Filme filme : aluguel.getFilmes()) {
            myStmt3.setInt(1, aluguel.getIdAluguel());
            myStmt3.setInt(2, filme.getIdFilme());

            myStmt3.execute();
            myStmt3.clearParameters();
        }

        conn.commit();
    }

    @Override
    public Aluguel find(Connection conn, Integer idAluguel) throws Exception {
/*
        PreparedStatement myStmt = conn.prepareStatement("select * from en_aluguel where id_aluguel = ?");
*/
        PreparedStatement myStmt = conn.prepareStatement("select * from en_aluguel inner join en_cliente on (en_aluguel.id_cliente = en_cliente.id_cliente) where en_aluguel.id_aluguel = ?");

        myStmt.setInt(1, idAluguel);

        ResultSet myRs = myStmt.executeQuery();

        if (!myRs.next()) {
            return null;
        }

        Date date = myRs.getDate("data_aluguel");
        float valor = myRs.getFloat("valor");
        Integer idCliente = myRs.getInt("id_cliente");
        String nomeCliente = myRs.getString("nome");
        Cliente cliente = new Cliente(idCliente, nomeCliente);

        Collection<Filme> filmesC = new ArrayList<>();
        filmesC = new FilmeDAOImpl().listByIdAluguel(conn, idAluguel);
        List<Filme> filmes = new ArrayList<Filme>(filmesC);

        return new Aluguel(idAluguel, filmes, cliente, date, valor);
    }


    @Override
    public void delete(Connection conn, Integer idAluguel) throws Exception {
        // delete from re_aluguel_filme where id_aluguel = 11;
        PreparedStatement myStmt = conn.prepareStatement("delete from re_aluguel_filme where id_aluguel = ?");//excluindo primeiro na tabela de associação onde existe a referencia do aluguel

        myStmt.setInt(1, idAluguel);

        myStmt.execute();

        PreparedStatement myStmt2 = conn.prepareStatement("delete from en_aluguel where id_aluguel = ");

        myStmt.setInt(1, idAluguel);
        conn.commit();

    }


    @Override
    public Collection<Aluguel> list(Connection conn) throws Exception {

        PreparedStatement myStmt = conn.prepareStatement("select * from en_aluguel inner join en_cliente on (en_aluguel.id_cliente = en_cliente.id_cliente) order by id_aluguel");

        ResultSet myRs = myStmt.executeQuery();

        Collection<Aluguel> items = new ArrayList<>();

        while (myRs.next()) {
            Integer idAluguel = myRs.getInt("id_Aluguel");
            Collection<Filme> filmesCollection = new FilmeDAOImpl().listByIdAluguel(conn, idAluguel);
            List<Filme> filmes = new ArrayList<>(filmesCollection);
           // Cliente cliente = new Cliente();
            //cliente =  myRs.getInt("id_cliente");
            //Integer idCliente = myRs.getInt("id_cliente");
            Cliente cliente = new Cliente();
            cliente.setIdCliente(myRs.getInt("id_cliente"));
            cliente.setNome(myRs.getString("nome"));
            Date date = myRs.getDate("data_aluguel");

            float valor = myRs.getFloat("valor");

            items.add(new Aluguel(idAluguel, filmes, cliente, date, valor));
        }

        return items;
    }
}
