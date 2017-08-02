package sqlite;

import objectos.Machina;
import pojos.ArtigoLinhaPlanOUAtraso;
import pojos.ArtigoOSBO;
import utils.Campos;
import utils.Constantes;

import java.sql.*;
import java.util.ArrayList;

@SuppressWarnings("UnusedReturnValue")
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
        Connection connection;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + Constantes.NOME_BASE_DADOS_SQL);
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
                + Campos.DTOPER + " text not null, "
                + Campos.SECCAO + " text not null, "
                + Campos.PECASPRODZ + " integer not null "
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

        comandoSql = "CREATE TABLE IF NOT EXISTS " + Campos.TABELA_MACHINA + "("
                + Campos._ID + " integer primary key autoincrement, "
                + Campos.SECCAO + " text not null, "
                + Campos.CODIGO + " text not null, "
                + Campos.FUNCAO + " text not null, "
                + Campos.NOME + " text not null, "
                + Campos.ORDEM + " integer not null"
                + ")";
        try {
            statement.executeUpdate(comandoSql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("***** ERRO AO CRIAR A TABELA " + Campos.TABELA_MACHINA + " *****");
            return null;
        }

        comandoSql = "CREATE TABLE IF NOT EXISTS " + Campos.TABELA_STACK + "("
                + Campos._ID + " integer primary key autoincrement, "
                + Campos.BOSTAMP + " text not null, "
                + Campos.UNIXTIME + " integer not null"
                + ")";
        try {
            statement.executeUpdate(comandoSql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("***** ERRO AO CRIAR A TABELA " + Campos.TABELA_STACK + " *****");
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
                c.prepareStatement("delete from " + Campos.TABELA_MACHINA).executeUpdate();
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
                                + Campos.DTOPER + ", "
                                + Campos.SECCAO + ", "
                                + Campos.PECASPRODZ
                                + ")"
                                + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
                preparedStatement.setString(15, osbo.getDtoper());
                preparedStatement.setString(16, osbo.getSeccao());
                preparedStatement.setInt(17, osbo.getPecasprodz());

                t = preparedStatement.executeUpdate();
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return t;
    }


    public int guardarMachina(Machina machina) {
        Connection con = connect();
        int t = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "insert into " + Campos.TABELA_MACHINA + " ("
                                + Campos.SECCAO + ", "
                                + Campos.CODIGO + ", "
                                + Campos.FUNCAO + ", "
                                + Campos.NOME + ", "
                                + Campos.ORDEM
                                + ")"
                                + " VALUES (?,?,?,?,?)");
                preparedStatement.setString(1, machina.getSeccao());
                preparedStatement.setString(2, machina.getCodigo());
                preparedStatement.setString(3, machina.getFuncao());
                preparedStatement.setString(4, machina.getNome());
                preparedStatement.setLong(5, machina.getOrdem());
                t = preparedStatement.executeUpdate();
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public int colocar_OSBO_em_Stack(ArtigoOSBO osbo) {
        long unixTime = System.currentTimeMillis();
        Connection con = connect();
        int t = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "insert into " + Campos.TABELA_STACK + " ("
                                + Campos.BOSTAMP + ", "
                                + Campos.UNIXTIME
                                + ")"
                                + " VALUES (?,?)");
                preparedStatement.setString(1, osbo.getBostamp());
                preparedStatement.setLong(2, unixTime);
                t = preparedStatement.executeUpdate();
                preparedStatement.close();
                con.close();
                System.out.println("colocar_OSBO_em_Stack: " + osbo.getObrano() + " dataop " + osbo.getDtoper()
                        + ", ordem " + osbo.getOrdem() + ", unix " + unixTime + " guardado em stack");
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
                                + Campos.SECCAO + "=?, "
                                + Campos.DTOPER + "=?,"
                                + Campos.PECAS + "=?,"
                                + Campos.PECASPRODZ + "=?"
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
                preparedStatement.setString(14, osbo.getDtoper());
                preparedStatement.setInt(15, osbo.getPecas());
                preparedStatement.setInt(16, osbo.getPecasprodz());
                preparedStatement.setString(17, osbo.getBostamp());

                t = preparedStatement.executeUpdate();
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return t;
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

    public ArrayList<ArtigoOSBO> getListaArtigoOSBO(String seccao, String estado, String filtroObra) {
        Connection con = connect();
        ArrayList<ArtigoOSBO> lista = new ArrayList<>();
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "select * from " + Campos.TABELA_OSBO
                                + " where " + Campos.SECCAO + " = ? AND " + Campos.ESTADO + " = ? AND " + Campos.FREF + " like ?"
                );
                preparedStatement.setString(1, seccao);
                preparedStatement.setString(2, estado);
                preparedStatement.setString(3, filtroObra.trim() + "%");
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
                            , resultSet.getString(Campos.DTOPER)
                            , resultSet.getInt(Campos.PECASPRODZ)
                    );
                    lista.add(osbo);
                }
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("getListaArtigoOSBO('" + seccao + "', '" + estado + "', '" + filtroObra + "') : " + lista.size());
        return lista;
    }

    public ArrayList<ArtigoOSBO> get_Lista_OS_Atrasadas(String data, String filtro, String seccao, String estado) {
        Connection con = connect();
        ArrayList<ArtigoOSBO> lista = new ArrayList<>();
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "select * from " + Campos.TABELA_OSBO
                                + " where " + Campos.DTOPER + " < ? AND " + Campos.FREF + " LIKE ? AND " + Campos.SECCAO + " = ? AND " + Campos.ESTADO + " = ? "
                                + "AND " + Campos.DTOPER + " <> ? "
                                + " order by " + Campos.SECCAO + ", " + Campos.DTCORTEF + ", " + Campos.ORDEM
                );
                preparedStatement.setString(1, data);
                preparedStatement.setString(2, filtro.trim() + "%");
                preparedStatement.setString(3, seccao);
                preparedStatement.setString(4, estado);
                preparedStatement.setString(5, "1900-01-01 00:00:00");
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
                            , resultSet.getString(Campos.DTOPER)
                            , resultSet.getInt(Campos.PECASPRODZ)
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

    public ArrayList<ArtigoLinhaPlanOUAtraso> get_Lista_OS_Por_Planear(String filtro, String seccao, String estado) {
        Connection con = connect();
        ArrayList<ArtigoLinhaPlanOUAtraso> lista = new ArrayList<>();
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "select * from " + Campos.TABELA_OSBO
                                + " where " + Campos.FREF + " like ? AND " + Campos.SECCAO + " = ? AND " + Campos.ESTADO + " = ? AND " + Campos.DTOPER + " = ?"
                                + " order by " + Campos.SECCAO + ", " + Campos.OBRANO
                );
                preparedStatement.setString(1, filtro.trim() + "%");
                preparedStatement.setString(2, seccao);
                preparedStatement.setString(3, estado);
                preparedStatement.setString(4, "1900-01-01 00:00:00");
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
                            , resultSet.getInt(Campos.PECAS)
                            , resultSet.getString(Campos.ESTADO)
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

    public int getQtdPedidaData(String data, String seccao, String estado) {
        Connection con = connect();
        int qtt = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "select SUM(" + Campos.PECAS + ") as " + Campos.PECAS + " from " + Campos.TABELA_OSBO
                                + " where " + Campos.DTOPER + "=? AND " + Campos.SECCAO + "=? AND " + Campos.ESTADO + "=?"
                );
                preparedStatement.setString(1, data);
                preparedStatement.setString(2, seccao);
                preparedStatement.setString(3, estado);
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

    public int getQtdProduzidaData(String data, String seccao, String estado) {
        Connection con = connect();
        int qtt = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "select sum(" + Campos.PECASPRODZ + ") as " + Campos.PECASPRODZ
                                + " from " + Campos.TABELA_OSBO
                                + " where " + Campos.DTOPER + "=? AND " + Campos.SECCAO + "=? AND " + Campos.ESTADO + "=?"
                );
                preparedStatement.setString(1, data);
                preparedStatement.setString(2, seccao);
                preparedStatement.setString(3, estado);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    qtt = resultSet.getInt(Campos.PECASPRODZ);
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
//        System.out.println("getQtdProduzidaData(" + data + ", " + seccao + ", " + estado + ") = " + qtt);
        return qtt;
    }

    public ArtigoOSBO select_Top1_Stack() {
        Connection con = connect();
        ArtigoOSBO artigoOSBO = null;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "select o.*, s." + Campos.UNIXTIME
                                + " from " + Campos.TABELA_OSBO + " o"
                                + " inner join " + Campos.TABELA_STACK + " s on o." + Campos.BOSTAMP + " = s." + Campos.BOSTAMP
                                + " order by s." + Campos.UNIXTIME
                                + " limit 1 "
                );
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String bostamp = resultSet.getString(Campos.BOSTAMP);
                    String estado = resultSet.getString(Campos.ESTADO);
                    int cor = resultSet.getInt(Campos.COR);
                    int ordem = resultSet.getInt(Campos.ORDEM);
                    String dtoper = resultSet.getString(Campos.DTOPER);
                    long unixtime = resultSet.getLong(Campos.UNIXTIME);
                    artigoOSBO = new ArtigoOSBO(bostamp, estado, cor, ordem, dtoper, unixtime);
                }
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return artigoOSBO;
    }

    public int deleteStack(ArtigoOSBO artigoOSBO) {
        Connection con = connect();
        int t = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "delete from " + Campos.TABELA_STACK
                                + " where " + Campos.BOSTAMP + " = ? AND " + Campos.UNIXTIME + " = ?"
                );
                preparedStatement.setString(1, artigoOSBO.getBostamp());
                preparedStatement.setLong(2, artigoOSBO.getUnixtime());
                t = preparedStatement.executeUpdate();
                preparedStatement.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return t;
    }

    public int select_max_ordem(String seccao, String estado, String dataDeOperacao) {
        Connection con = connect();
        int ordem = 0;
        if (con != null) {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "select MAX(" + Campos.ORDEM + ") as " + Campos.ORDEM
                                + " from " + Campos.TABELA_OSBO
                                + " where " + Campos.SECCAO + " =? AND " + Campos.ESTADO + " =? AND " + Campos.DTOPER + " =?"
                );
                preparedStatement.setString(1, seccao);
                preparedStatement.setString(2, estado);
                preparedStatement.setString(3, dataDeOperacao);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    ordem = resultSet.getInt(Campos.ORDEM);
                }
                preparedStatement.close();
                con.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("select_max_ordem('" + seccao + "','" + estado + "','" + dataDeOperacao + "' = " + ordem);
        return ordem;
    }
}
