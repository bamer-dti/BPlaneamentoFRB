package sqlite;

import pojos.ArtigoOSBO;
import utils.Campos;

import java.sql.*;
import java.util.ArrayList;

public class DBSQLite {
    @SuppressWarnings("unused")
    private static final String TAG = DBSQLite.class.getSimpleName() + ": ";

    private static DBSQLite sqlDefaults;

    public static DBSQLite getInstancia() {
        if (sqlDefaults == null) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            sqlDefaults = new DBSQLite();
            sqlDefaults.connect();
        }
        return sqlDefaults;
    }

    private Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:firebase.db");
            Statement statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("***** ERRO AO CONECTAR À BASE DE DADOS *****");
            return null;
        }


//        String comandoSql = "DROP TABLE " + TABELA_PREFS;
//        statement.executeUpdate(comandoSql);

        String comandoSql = "CREATE TABLE IF NOT EXISTS " + Campos.TABELA_OSBO + "("
                + Campos._ID + " integer primary key autoincrement, "
                + Campos.BOSTAMP + " text not null, "
                + Campos.COR + " integer not null, "
                + Campos.DTCLIENTE + " text not null, "
                + Campos.DTCORTEF + " text not null, "
                + Campos.DTEMBALA + " text not null, "
                + Campos.DTEXPEDI + " text not null, "
                + Campos.DTTRANSF + " text not null, "
                + Campos.ESTADO + " text not null, "
                + Campos.FREF + " text not null, "
                + Campos.NMFREF + " text not null, "
                + Campos.OBRANO + " integer not null, "
                + Campos.OBS + " text not null, "
                + Campos.ORDEM + " integer not null, "
                + Campos.SECCAO + " text not null"
                + ")";
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(comandoSql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("***** ERRO AO CRIAR A TABELA " + Campos.TABELA_OSBO + " *****");
            return null;
        }

        comandoSql = "CREATE TABLE IF NOT EXISTS " + Campos.TABELA_OSBI + "("
                + Campos._ID + " integer primary key autoincrement, "
                + Campos.BOSTAMP + " text not null, "
                + Campos.BISTAMP + " text not null, "
                + Campos.DESIGN + " text not null, "
                + Campos.DIM + " text not null, "
                + Campos.FAMILIA + " text not null, "
                + Campos.MK + " text not null, "
                + Campos.NUMLINHA + " text not null, "
                + Campos.QTT + " real not null, "
                + Campos.REF + " text not null, "
                + Campos.TIPO + " text not null"
                + ")";
        try {
            statement.executeUpdate(comandoSql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("***** ERRO AO CRIAR A TABELA " + Campos.TABELA_OSBI + " *****");
            return null;
        }

        comandoSql = "CREATE TABLE IF NOT EXISTS " + Campos.TABELA_OSPROD + "("
                + Campos._ID + " integer primary key autoincrement, "
                + Campos.BOSTAMP + " text not null, "
                + Campos.BISTAMP + " text not null, "
                + Campos.DESIGN + " text not null, "
                + Campos.DIM + " text not null, "
                + Campos.MK + " text not null, "
                + Campos.NUMLINHA + " text not null, "
                + Campos.QTT + " real not null, "
                + Campos.REF + " text not null"
                + ")";
        try {
            statement.executeUpdate(comandoSql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("***** ERRO AO CRIAR A TABELA " + Campos.TABELA_OSPROD + " *****");
            return null;
        }

        comandoSql = "CREATE TABLE IF NOT EXISTS " + Campos.TABELA_OSTIMER + "("
                + Campos._ID + " integer primary key autoincrement, "
                + Campos.BOSTAMP + " text not null, "
                + Campos.BISTAMP + " text not null, "
                + Campos.ESTADO + " text not null, "
                + Campos.FREF + " text not null, "
                + Campos.LASTTIME + " integer not null, "
                + Campos.MAQUINA + " text not null, "
                + Campos.OBRANO + " int not null, "

                + Campos.OPERADOR + " text not null, "
                + Campos.POSICAO + " int not null, "
                + Campos.SECCAO + " text not null, "
                + Campos.UNIXTIME + " integer not null"
                + ")";
        try {
            statement.executeUpdate(comandoSql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("***** ERRO AO CRIAR A TABELA " + Campos.TABELA_OSTIMER + " *****");
            return null;
        }
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return connection;
    }

    public int guardar(ArtigoOSBO osbo) {
        Connection con = connect();
        int t = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "insert into " + Campos.TABELA_OSBO + " ("
                                + Campos.BOSTAMP + ", "
                                + Campos.COR + ", "
                                + Campos.DTCLIENTE + ", "
                                + Campos.DTCORTEF + ", "
                                + Campos.DTEMBALA + ", "
                                + Campos.DTEXPEDI + ", "
                                + Campos.DTTRANSF + ", "
                                + Campos.ESTADO + ", "
                                + Campos.FREF + ", "
                                + Campos.NMFREF + ", "
                                + Campos.OBRANO + ", "
                                + Campos.OBS + ", "
                                + Campos.ORDEM + ", "
                                + Campos.SECCAO + ")"
                                + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                preparedStatement.setString(1, osbo.getBostamp());
                preparedStatement.setInt(2, osbo.getCor());
                preparedStatement.setString(3, osbo.getDtcliente());
                preparedStatement.setString(4, osbo.getDtcortef());
                preparedStatement.setString(5, osbo.getDtembala());
                preparedStatement.setString(6, osbo.getDtexpedi());
                preparedStatement.setString(7, osbo.getDttransf());
                preparedStatement.setString(8, osbo.getEstado());
                preparedStatement.setString(9, osbo.getFref());
                preparedStatement.setString(10, osbo.getNmfref());
                preparedStatement.setInt(11, osbo.getObrano());
                preparedStatement.setString(12, osbo.getObs());
                preparedStatement.setInt(13, osbo.getOrdem());
                preparedStatement.setString(14, osbo.getSeccao());

                t = preparedStatement.executeUpdate();
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public void resetDados() {
        Connection c = connect();
        try {
            if (c != null) {
                c.prepareStatement("delete from " + Campos.TABELA_OSBO).executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int actualizar(ArtigoOSBO osbo) {
        Connection con = connect();
        int t = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "update " + Campos.TABELA_OSBO + " SET "
                                + Campos.COR + "=?, "
                                + Campos.DTCLIENTE + "=?, "
                                + Campos.DTCORTEF + "=?, "
                                + Campos.DTEMBALA + "=?, "
                                + Campos.DTEXPEDI + "=?, "
                                + Campos.DTTRANSF + "=?, "
                                + Campos.ESTADO + "=?, "
                                + Campos.FREF + "=?, "
                                + Campos.NMFREF + "=?, "
                                + Campos.OBRANO + "=?, "
                                + Campos.OBS + "=?, "
                                + Campos.ORDEM + "=?, "
                                + Campos.SECCAO + "=?"
                                + " where " + Campos.BOSTAMP + "=?"
                );
                preparedStatement.setInt(1, osbo.getCor());
                preparedStatement.setString(2, osbo.getDtcliente());
                preparedStatement.setString(3, osbo.getDtcortef());
                preparedStatement.setString(4, osbo.getDtembala());
                preparedStatement.setString(5, osbo.getDtexpedi());
                preparedStatement.setString(6, osbo.getDttransf());
                preparedStatement.setString(7, osbo.getEstado());
                preparedStatement.setString(8, osbo.getFref());
                preparedStatement.setString(9, osbo.getNmfref());
                preparedStatement.setInt(10, osbo.getObrano());
                preparedStatement.setString(11, osbo.getObs());
                preparedStatement.setInt(12, osbo.getOrdem());
                preparedStatement.setString(13, osbo.getSeccao());
                preparedStatement.setString(14, osbo.getBostamp());

                t = preparedStatement.executeUpdate();
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public int remover(ArtigoOSBO osbo) {
        Connection con = connect();
        int t = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "delete from " + Campos.TABELA_OSBO
                                + " where " + Campos.BOSTAMP + "=?"
                );
                preparedStatement.setString(1, osbo.getBostamp());
                t = preparedStatement.executeUpdate();
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public ArrayList<ArtigoOSBO> getListaArtigoOSBO(String seccao) {
        Connection con = connect();
        ArrayList<ArtigoOSBO> lista = new ArrayList<>();
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "select * from " + Campos.TABELA_OSBO
                                + " where " + Campos.SECCAO + "=?"
                );
                preparedStatement.setString(1, seccao);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    ArtigoOSBO osbo = new ArtigoOSBO(resultSet.getString(Campos.BOSTAMP)
                            , resultSet.getInt(Campos.OBRANO)
                            , resultSet.getString(Campos.FREF)
                            , resultSet.getString(Campos.NMFREF)
                            , resultSet.getString(Campos.ESTADO)
                            , resultSet.getString(Campos.SECCAO)
                            , resultSet.getString(Campos.OBS)
                            , resultSet.getInt(Campos.COR)
                            , resultSet.getString(Campos.DTTRANSF)
                            , resultSet.getString(Campos.DTEMBALA)
                            , resultSet.getString(Campos.DTEXPEDI)
                            , resultSet.getString(Campos.DTCORTEF)
                            , resultSet.getInt(Campos.ORDEM)
                            , resultSet.getString(Campos.DTCLIENTE)
                    );
                    lista.add(osbo);
                }
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return lista;
    }
}
