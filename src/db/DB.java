package db;

import enums.UserRole;
import java.util.ArrayList;
import models.Empregado.Assalariado;
import models.Empregado.Empregado;
import models.Endereco;
import models.Usuario;

/**
 *
 * @author lucas
 */
public class DB {
    public static ArrayList<Empregado> empregados = new ArrayList<>();
    
    
    public static void preparade() {
        Endereco end = new Endereco();
        end.setLogradouro("Alvaro Otacilio");
        end.setComplemento("AP 402");
        end.setNumero("3379");
        end.setCidade("Maceió");
        end.setCep("57035180");
        end.setUf("AL");
        Assalariado admin = new Assalariado();
        admin.setAccess_user(new Usuario("admin", "1234", UserRole.ADMIN));
        admin.setName("Lucas");
        admin.setEndereco(end);
        admin.setSalarioMensal(8000.0);
        empregados.add(admin);
    }
}
