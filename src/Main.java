import com.sun.source.tree.ReturnTree;
import dao.AluguelDAO;
import dao.ClienteDAO;
import dao.FilmeDAO;
import dao.jdbc.AluguelDAOImpl;
import dao.jdbc.ClienteDAOImpl;
import dao.jdbc.FilmeDAOImpl;
import entidades.Aluguel;
import entidades.Cliente;
import entidades.Filme;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;


public class Main {

    public static <Return> void main(String[] args) {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost/aluguel_filmes", "postgres", "postgres");
            conn.setAutoCommit(false);

            //Demonstrar o funcionamento aqui

            Date date = new Date();
            ClienteDAO clienteDAO = new ClienteDAOImpl();
            AluguelDAO aluguelDAO = new AluguelDAOImpl();
            FilmeDAO filmeDAO = new FilmeDAOImpl();


            Cliente cliente = new Cliente();
            cliente.setNome("Julia Stein");
            clienteDAO.insert(conn, cliente);
            //  clienteDAO.delete(conn, cliente.getIdCliente());
            clienteDAO.find(conn, cliente.getIdCliente());


            Filme filme = new Filme();
            filme.setDataLancamento(date);
            filme.setNome("Toy Store");
            filme.setDescricao("Filme para adultos legais");


            filmeDAO.insert(conn, filme);
            filmeDAO.edit(conn, filme);
            //filmeDAO.delete(conn , filme.getIdFilme());

            Filme result = filmeDAO.find(conn, filme.getIdFilme());
            // System.out.println(result.getNome());


            List<Filme> filmes = new ArrayList<>(filmeDAO.list(conn));
            List<Filme> filmesEscolhidos = new ArrayList<>();
            for (Filme filmeI : filmes) {
                // System.out.print(filmeI.getIdFilme());
                // System.out.println(filmeI.getNome());
            }
            filmesEscolhidos.add(filmes.get(1));
            filmesEscolhidos.add(filmes.get(2));


            Aluguel aluguel = new Aluguel();
            aluguel.setIdAluguel(3);
            aluguel.setCliente(cliente);
            aluguel.setDataAluguel(date);
            aluguel.setValor(10.9F);
            aluguel.setFilmes(filmesEscolhidos);


            aluguelDAO.insert(conn, aluguel);
            aluguel = aluguelDAO.find(conn, aluguel.getIdAluguel());
            // System.out.println(aluguel.toString());
            // aluguelDAO.delete(conn, aluguel.getIdAluguel());

            List<Aluguel> alugueis = new ArrayList<>(aluguelDAO.list(conn));

            for (Aluguel alugueld : alugueis) {
                System.out.println(alugueld);
            }


            //teste update aluguel
            Aluguel aluguelU = aluguelDAO.find(conn, 4);//pesquisar pelo id do aluguel para edita-lo
            aluguelU.setValor(20F);
            Cliente cliente2 = new Cliente();
            cliente2.setIdCliente(2);
            cliente2.setNome("julia");
            aluguelU.setCliente(cliente2);

            List<Filme> filmesU = new ArrayList<>(filmeDAO.list(conn));
            List<Filme> filmesEscolhidosU = new ArrayList<>();

            filmesEscolhidosU.add(filmesU.get(0));
            filmesEscolhidosU.add(filmesU.get(3));

            aluguelU.setFilmes(filmesEscolhidosU);

            aluguelDAO.edit(conn, aluguelU);


        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Fim do teste.");
    }
}