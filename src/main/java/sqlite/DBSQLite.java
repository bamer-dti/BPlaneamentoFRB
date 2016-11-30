package sqlite;

import pojos.ArtigoLinhaPlanOUAtraso;
import pojos.ArtigoOSBO;
import pojos.ArtigoOSPROD;
import pojos.ArtigoOSTIMER;
import utils.Campos;

import java.sql.*;
import java.util.ArrayList;

public class DBSQLite {
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
        Connection connection;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:firebase.db");
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
                + Campos.PECAS + " integer not null, "
                + Campos.SECCAO + " text not null"
                + ")";
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(comandoSql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("***** ERRO AO CRIAR A TABELA " + Campos.TABELA_OSBO + " *****");
            return null;
        }

        comandoSql = "CREATE TABLE IF NOT EXISTS " + Campos.TABELA_OSBOPLAN + "("
                + Campos._ID + " integer primary key autoincrement, "
                + Campos.BOSTAMP + " text not null, "
                + Campos.DTCLIENTE + " text not null, "
                + Campos.DTEXPEDI + " text not null, "
                + Campos.FREF + " text not null, "
                + Campos.NMFREF + " text not null, "
                + Campos.OBRANO + " integer not null, "
                + Campos.OBS + " text not null, "
                + Campos.QTT + " integer not null, "
                + Campos.SECCAO + " text not null"
                + ")";
        try {
            statement = connection.createStatement();
            statement.executeUpdate(comandoSql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("***** ERRO AO CRIAR A TABELA " + Campos.TABELA_OSBOPLAN + " *****");
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
                + Campos.STAMP + " text not null, "
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

    public void resetDados() {
        Connection c = connect();
        try {
            if (c != null) {
                c.prepareStatement("delete from " + Campos.TABELA_OSBO).executeUpdate();
                c.prepareStatement("delete from " + Campos.TABELA_OSBOPLAN).executeUpdate();
//                c.prepareStatement("delete from " + Campos.TABELA_OSBI).executeUpdate();
                c.prepareStatement("delete from " + Campos.TABELA_OSPROD).executeUpdate();
                c.prepareStatement("delete from " + Campos.TABELA_OSTIMER).executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int guardarOSBO(ArtigoOSBO osbo) {
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
                                + Campos.PECAS + ", "
                                + Campos.SECCAO + ")"
                                + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
                preparedStatement.setInt(14, osbo.getPecas());
                preparedStatement.setString(15, osbo.getSeccao());

                t = preparedStatement.executeUpdate();
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public int guardarOSBOPLAN(ArtigoLinhaPlanOUAtraso artigoLinhaPlanOUAtraso) {
        Connection con = connect();
        int t = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "insert into " + Campos.TABELA_OSBOPLAN + " ("
                                + Campos.BOSTAMP + ", "
                                + Campos.DTCLIENTE + ", "
                                + Campos.DTEXPEDI + ", "
                                + Campos.FREF + ", "
                                + Campos.NMFREF + ", "
                                + Campos.OBRANO + ", "
                                + Campos.OBS + ", "
                                + Campos.SECCAO + ", "
                                + Campos.QTT + ")"
                                + " VALUES (?,?,?,?,?,?,?,?,?)");
                preparedStatement.setString(1, artigoLinhaPlanOUAtraso.getBostamp());
                preparedStatement.setString(2, artigoLinhaPlanOUAtraso.getDtcliente());
                preparedStatement.setString(3, artigoLinhaPlanOUAtraso.getDtexpedi());
                preparedStatement.setString(4, artigoLinhaPlanOUAtraso.getFref());
                preparedStatement.setString(5, artigoLinhaPlanOUAtraso.getNmfref());
                preparedStatement.setInt(6, artigoLinhaPlanOUAtraso.getObrano());
                preparedStatement.setString(7, artigoLinhaPlanOUAtraso.getObs());
                preparedStatement.setString(8, artigoLinhaPlanOUAtraso.getSeccao());
                preparedStatement.setInt(9, artigoLinhaPlanOUAtraso.getQtt());

                t = preparedStatement.executeUpdate();
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public int guardarOSPROD(ArtigoOSPROD osprod) {
        Connection con = connect();
        int t = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "insert into " + Campos.TABELA_OSPROD + " ("
                                + Campos.BOSTAMP + ", "
                                + Campos.BISTAMP + ", "
                                + Campos.DESIGN + ", "
                                + Campos.DIM + ", "
                                + Campos.MK + ", "
                                + Campos.NUMLINHA + ", "
                                + Campos.QTT + ", "
                                + Campos.REF
                                + ")"
                                + " VALUES (?,?,?,?,?,?,?,?)");
                preparedStatement.setString(1, osprod.getBostamp());
                preparedStatement.setString(2, osprod.getBistamp());
                preparedStatement.setString(3, osprod.getDesign());
                preparedStatement.setString(4, osprod.getDim());
                preparedStatement.setString(5, osprod.getMk());
                preparedStatement.setString(6, osprod.getNumlinha());
                preparedStatement.setInt(7, osprod.getQtt());
                preparedStatement.setString(8, osprod.getRef());

                t = preparedStatement.executeUpdate();
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public int guardarOSTIMER(ArtigoOSTIMER ostimer) {
        Connection con = connect();
        int t = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "insert into " + Campos.TABELA_OSTIMER + " ("
                                + Campos.BOSTAMP + ", "
                                + Campos.STAMP + ", "
                                + Campos.ESTADO + ", "
                                + Campos.FREF + ", "
                                + Campos.LASTTIME + ", "
                                + Campos.MAQUINA + ", "
                                + Campos.OBRANO + ", "
                                + Campos.OPERADOR + ", "
                                + Campos.POSICAO + ", "
                                + Campos.SECCAO + ", "
                                + Campos.UNIXTIME
                                + ")"
                                + " VALUES (?,?,?,?,?,?,?,?,?,?,?)");
                preparedStatement.setString(1, ostimer.getBostamp());
                preparedStatement.setString(2, ostimer.getStamp());
                preparedStatement.setString(3, ostimer.getEstado());
                preparedStatement.setString(4, ostimer.getFref());
                preparedStatement.setLong(5, ostimer.getLasttime());
                preparedStatement.setString(6, ostimer.getMaquina());
                preparedStatement.setInt(7, ostimer.getObrano());
                preparedStatement.setString(8, ostimer.getOperador());
                preparedStatement.setInt(9, ostimer.getPosicao());
                preparedStatement.setString(10, ostimer.getSeccao());
                preparedStatement.setLong(11, ostimer.getUnixtime());

                t = preparedStatement.executeUpdate();
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public int actualizarOSBO(ArtigoOSBO osbo) {
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

    public int actualizarOSBOPLAN(ArtigoLinhaPlanOUAtraso osbo) {
        Connection con = connect();
        int t = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "update " + Campos.TABELA_OSBOPLAN + " SET "
                                + Campos.DTCLIENTE + "=?, "
                                + Campos.DTEXPEDI + "=?, "
                                + Campos.FREF + "=?, "
                                + Campos.NMFREF + "=?, "
                                + Campos.OBRANO + "=?, "
                                + Campos.OBS + "=?, "
                                + Campos.QTT + "=?, "
                                + Campos.SECCAO + "=?"
                                + " where " + Campos.BOSTAMP + "=?"
                );
                preparedStatement.setString(1, osbo.getDt1());
                preparedStatement.setString(2, osbo.getDt2());
                preparedStatement.setString(3, osbo.getFref());
                preparedStatement.setString(4, osbo.getNmfref());
                preparedStatement.setInt(5, osbo.getObrano());
                preparedStatement.setString(6, osbo.getObs());
                preparedStatement.setInt(7, osbo.getQtt());
                preparedStatement.setString(8, osbo.getSeccao());
                preparedStatement.setString(9, osbo.getBostamp());

                t = preparedStatement.executeUpdate();
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public int actualizarOSPROD(ArtigoOSPROD osprod) {
        Connection con = connect();
        int t = 0;
        if (con != null) {
            try {
                if (existeArtigoOSPROD(osprod) != 0) {
                    PreparedStatement preparedStatement = con.prepareStatement(
                            "update " + Campos.TABELA_OSPROD + " SET "
                                    + Campos.DESIGN + "=?, "
                                    + Campos.DIM + "=?, "
                                    + Campos.MK + "=?, "
                                    + Campos.NUMLINHA + "=?, "
                                    + Campos.QTT + "=?, "
                                    + Campos.REF + "=? "
                                    + " where " + Campos.BISTAMP + "=?"
                    );
                    preparedStatement.setString(1, osprod.getDesign());
                    preparedStatement.setString(2, osprod.getDim());
                    preparedStatement.setString(3, osprod.getMk());
                    preparedStatement.setString(4, osprod.getNumlinha());
                    preparedStatement.setInt(5, osprod.getQtt());
                    preparedStatement.setString(6, osprod.getRef());
                    preparedStatement.setString(7, osprod.getBistamp());

                    t = preparedStatement.executeUpdate();

                    preparedStatement.close();
                    con.close();
                } else {
                    guardarOSPROD(osprod);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    private int existeArtigoOSPROD(ArtigoOSPROD artigoOSPROD) {
        Connection con = connect();
        int qtt = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "select count(" + Campos.BOSTAMP + ") as " + Campos.QTT
                                + " from " + Campos.TABELA_OSPROD
                                + " where " + Campos.BOSTAMP + " =? AND " + Campos.BISTAMP + " =? "
                );
                preparedStatement.setString(1, artigoOSPROD.getBostamp());
                preparedStatement.setString(2, artigoOSPROD.getBistamp());

                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    qtt = rs.getInt(Campos.QTT);
                }
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return qtt;
    }

    public int removerOSBO(ArtigoOSBO artigoOSBO) {
        Connection con = connect();
        int t = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "delete from " + Campos.TABELA_OSBO
                                + " where " + Campos.BOSTAMP + "=?"
                );
                preparedStatement.setString(1, artigoOSBO.getBostamp());
                t = preparedStatement.executeUpdate();
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public int removerOSBOPLAN(ArtigoLinhaPlanOUAtraso artigoLinhaPlanOUAtraso) {
        Connection con = connect();
        int t = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "delete from " + Campos.TABELA_OSBOPLAN
                                + " where " + Campos.BOSTAMP + "=?"
                );
                preparedStatement.setString(1, artigoLinhaPlanOUAtraso.getBostamp());
                t = preparedStatement.executeUpdate();
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public int removerOSTIMER(ArtigoOSTIMER artigoOSTIMER) {
        Connection con = connect();
        int t = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "delete from " + Campos.TABELA_OSTIMER
                                + " where " + Campos.STAMP + "=?"
                );
                preparedStatement.setString(1, artigoOSTIMER.getStamp());
                t = preparedStatement.executeUpdate();
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public int removerOSTIMERviaBostamp(String bostamp) {
        Connection con = connect();
        int t = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "delete from " + Campos.TABELA_OSTIMER
                                + " where " + Campos.BOSTAMP + "=?"
                );
                preparedStatement.setString(1, bostamp);
                t = preparedStatement.executeUpdate();
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public int removerOSPROD(ArtigoOSPROD artigoOSPROD) {
        Connection con = connect();
        int t = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "delete from " + Campos.TABELA_OSPROD
                                + " where " + Campos.BOSTAMP + "=? AND " + Campos.BISTAMP + "=?"
                );
                preparedStatement.setString(1, artigoOSPROD.getBostamp());
                preparedStatement.setString(2, artigoOSPROD.getBistamp());
                t = preparedStatement.executeUpdate();
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public int removerOSPRODviaBostamp(String bostamp) {
        Connection con = connect();
        int t = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "delete from " + Campos.TABELA_OSPROD
                                + " where " + Campos.BOSTAMP + "=? "
                );
                preparedStatement.setString(1, bostamp);
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
                                + " where " + Campos.SECCAO + " = ? "
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
                            , resultSet.getInt(Campos.PECAS)
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

    public ArrayList<ArtigoOSBO> getListaArtigoOSBOAtrasados(String data, String filtro) {
        Connection con = connect();
        ArrayList<ArtigoOSBO> lista = new ArrayList<>();
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "select * from " + Campos.TABELA_OSBO
                                + " where " + Campos.DTCORTEF + " < ? AND " + Campos.FREF + " LIKE ?"
                                + " order by " + Campos.SECCAO + ", " + Campos.DTCORTEF + ", " + Campos.ORDEM
                );
                preparedStatement.setString(1, data);
                preparedStatement.setString(2, filtro.trim() + "%");
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
                            , resultSet.getInt(Campos.PECAS)
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

    public ArrayList<ArtigoLinhaPlanOUAtraso> getListaOSBOPLAN(String filtro) {
        Connection con = connect();
        ArrayList<ArtigoLinhaPlanOUAtraso> lista = new ArrayList<>();
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "select * from " + Campos.TABELA_OSBOPLAN
                                + " where " + Campos.FREF + " like ?"
                                + " order by " + Campos.SECCAO + ", " + Campos.OBRANO
                );
                preparedStatement.setString(1, filtro.trim() + "%");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    ArtigoLinhaPlanOUAtraso osbo = new ArtigoLinhaPlanOUAtraso(resultSet.getString(Campos.BOSTAMP)
                            , resultSet.getInt(Campos.OBRANO)
                            , resultSet.getString(Campos.FREF)
                            , resultSet.getString(Campos.NMFREF)
                            , resultSet.getString(Campos.SECCAO)
                            , resultSet.getString(Campos.OBS)
                            , resultSet.getString(Campos.DTEXPEDI)
                            , resultSet.getString(Campos.DTCLIENTE)
                            , resultSet.getInt(Campos.QTT)
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

    public int getQtdProduzidaBostamp(String bostamp) {
        Connection con = connect();
        int qtt = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "select sum(qtt) as " + Campos.QTT + " from " + Campos.TABELA_OSPROD
                                + " where " + Campos.BOSTAMP + "=?"
                );
                preparedStatement.setString(1, bostamp);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    qtt += resultSet.getInt(Campos.QTT);
                }
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return qtt;
    }

    public int getQtdPedidaData(String data, String seccao) {
        Connection con = connect();
        int qtt = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "select SUM(" + Campos.PECAS + ") as " + Campos.PECAS + " from " + Campos.TABELA_OSBO
                                + " where " + Campos.DTCORTEF + "=? AND " + Campos.SECCAO + "=?"
                );
                preparedStatement.setString(1, data);
                preparedStatement.setString(2, seccao);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    qtt += resultSet.getInt(Campos.PECAS);
                }
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return qtt;
    }

    public int getQtdProduzidaData(String data, String seccao) {
        Connection con = connect();
        int qtt = 0;
        if (con != null) {
            ArrayList<String> lista = new ArrayList<>();
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "select " + Campos.BOSTAMP + " from " + Campos.TABELA_OSBO
                                + " where " + Campos.DTCORTEF + "=? AND " + Campos.SECCAO + "=?"
                );
                preparedStatement.setString(1, data);
                preparedStatement.setString(2, seccao);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    lista.add(resultSet.getString(Campos.BOSTAMP));
                }
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            for (String bostamp : lista) {
                qtt += getQtdProduzidaBostamp(bostamp);
            }

            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return qtt;
    }

    public long getTempoTotal(String bostamp) {
        Connection con = connect();
        long qtt = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "select SUM(" + Campos.UNIXTIME + " - " + Campos.LASTTIME + ") as " + Campos.UNIXTIME + " from " + Campos.TABELA_OSTIMER
                                + " where " + Campos.BOSTAMP + "=? AND " + Campos.POSICAO + "=?"
                );
                preparedStatement.setString(1, bostamp);
                preparedStatement.setInt(2, 2);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    qtt += resultSet.getLong(Campos.UNIXTIME);
                }
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return qtt;
    }

    public int getUltimaPosicao(String bostamp) {
        Connection con = connect();
        int qtt = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "select " + Campos.POSICAO + " from " + Campos.TABELA_OSTIMER
                                + " where " + Campos.BOSTAMP + "=? "
                                + " order by " + Campos.UNIXTIME + " desc "
                                + " limit 1 "
                );
                preparedStatement.setString(1, bostamp);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    qtt = resultSet.getInt(Campos.POSICAO);
                }
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return qtt;
    }

    public long getUltimoTempo(String bostamp) {
        Connection con = connect();
        long qtt = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "select " + Campos.UNIXTIME + " from " + Campos.TABELA_OSTIMER
                                + " where " + Campos.BOSTAMP + "=? "
                                + " order by " + Campos.UNIXTIME + " desc "
                                + " limit 1 "
                );
                preparedStatement.setString(1, bostamp);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    qtt = resultSet.getLong(Campos.UNIXTIME);
                }
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return qtt;
    }

    public int getNumPlaneamento() {
        Connection con = connect();
        int qtt = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "select COUNT(" + Campos.BOSTAMP + ") as " + Campos.QTT + " from " + Campos.TABELA_OSBOPLAN
                );
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    qtt += resultSet.getInt(Campos.QTT);
                }
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return qtt;
    }

    public int getCountOSBOPLAN() {
        Connection con = connect();
        int cnt = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "select count(" + Campos.BOSTAMP + ") as " + Campos.QTT
                                + " from " + Campos.TABELA_OSBOPLAN
                );
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    cnt = resultSet.getInt(Campos.QTT);
                }
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cnt;
    }

    public int getCountOSTIMER(String bostamp) {
        Connection con = connect();
        int cnt = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "select count(" + Campos.BOSTAMP + ") as " + Campos.QTT
                                + " from " + Campos.TABELA_OSTIMER
                        + " where " + Campos.BOSTAMP + " =?"
                );
                preparedStatement.setString(1, bostamp);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    cnt = resultSet.getInt(Campos.QTT);
                }
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cnt;
    }
}
