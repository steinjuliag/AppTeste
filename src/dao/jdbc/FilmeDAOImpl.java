package dao.jdbc;

import dao.FilmeDAO;
import entidades.Cliente;
import entidades.Filme;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

public class FilmeDAOImpl implements FilmeDAO {
    @Override
    public void insert(Connection conn, Filme filme) throws Exception {
        PreparedStatement myStmt = conn.prepareStatement("insert into en_filme (id_filme, data_lancamento, nome, descricao ) values (?, ?, ?, ?)");

        Integer idFilme = this.getNextId(conn);

        myStmt.setInt(1, idFilme);
        myStmt.setDate(2, new java.sql.Date(filme.getDataLancamento().getTime()));
        myStmt.setString(3, filme.getNome());
        myStmt.setString(4, filme.getDescricao());

        myStmt.execute();
        conn.commit();

        filme.setIdFilme(idFilme);
    }

    @Override
    public Integer getNextId(Connection conn) throws Exception {
        PreparedStatement myStmt = conn.prepareStatement("select nextval('seq_en_filme')");

        ResultSet rs = myStmt.executeQuery();
        rs.next();

        return rs.getInt(1);
    }

    @Override
    public void edit(Connection conn, Filme filme) throws Exception {
        PreparedStatement myStmt = conn.prepareStatement("update en_filme set nome = (?) where id_filme = (?)");

        myStmt.setString(1, filme.getNome());
        myStmt.setInt(2, filme.getIdFilme());

        myStmt.execute();
        conn.commit();

    }

    @Override
    public void delete(Connection conn, Integer idFilme) throws Exception {
        PreparedStatement myStmt = conn.prepareStatement("delete from en_filme where id_filme = ?");

        myStmt.setInt(1, idFilme);

        myStmt.execute();
        conn.commit();
    }

    @Override
    public Filme find(Connection conn, Integer idFilme) throws Exception {
        PreparedStatement myStmt = conn.prepareStatement("select * from en_filme where id_filme = ?");

        myStmt.setInt(1, idFilme);

        ResultSet myRs = myStmt.executeQuery();

        if (!myRs.next()) {
            return null;
        }

        Date date;
        date = myRs.getDate("data_lancamento");
        String nome = myRs.getString("nome");
        String descricao = myRs.getString("descricao");

        return new Filme(idFilme, date, nome, descricao);

    }

    @Override
    public Collection<Filme> list(Connection conn) throws Exception {
        PreparedStatement myStmt = conn.prepareStatement("select * from en_filme order by nome");
        ResultSet myRs = myStmt.executeQuery();

        Collection<Filme> items = new ArrayList<>();

        while (myRs.next()) {
            Integer idFilme = myRs.getInt("id_filme");
            Date date;
            date = myRs.getDate("data_lancamento");
            String nome = myRs.getString("nome");
            String descricao = myRs.getString("descricao");

            items.add(new Filme(idFilme, date, nome, descricao));
        }

        return items;
    }

    @Override
    public Collection<Filme> listByIdAluguel(Connection conn, Integer idAluguel) throws Exception {

        PreparedStatement myStmt = conn.prepareStatement("select \n" +
                "en_filme.id_filme,en_filme.data_lancamento,en_filme.nome,en_filme.descricao\n" +
                "from en_filme\n" +
                "inner join re_aluguel_filme on (re_aluguel_filme.id_filme = en_filme.id_filme)\n" +
                "where re_aluguel_filme.id_aluguel = ?");

        myStmt.setInt(1, idAluguel);
        ResultSet myRs = myStmt.executeQuery();


        Collection<Filme> filmes = new ArrayList<>();

        while (myRs.next()) {
            Integer idFilme = myRs.getInt("id_filme");
            Date date;
            date = myRs.getDate("data_lancamento");
            String nome = myRs.getString("nome");
            String descricao = myRs.getString("descricao");

            filmes.add(new Filme(idFilme, date, nome, descricao));
        }

        return filmes;
    }

}
