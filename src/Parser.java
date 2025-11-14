import java.io.IOException;

import javax.swing.SpringLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parser {
    private String BOLSA_URL = "https://www.bolsasymercados.es/bme-exchange/es/Mercados-y-Cotizaciones/Acciones/Mercado-Continuo/Precios/ibex-35-ES0SI0000005";

    public void parse() {
        // Conecta a la página o carga HTML desde un String
        Document doc;
        try {
            doc = Jsoup.connect(BOLSA_URL).get();

            /* Creo que no carga la tabla porque usa javascript para generarla y
             * no esta en el html base */

            // Selecciona el tbody de la tabla
            Element tbody = doc.selectFirst(
                    "html.wrvcuatd.idc0_350 body#se_top main#main-content.Contenido div.container div.row div.col-sm-12 div#root div.table-responsive table.shares-table");

            if (tbody != null) {
                // Itera por cada fila
                Elements filas = tbody.select("tr");
                for (Element fila : filas) {
                    // Itera por cada celda de la fila
                    Elements celdas = fila.select("td");
                    for (Element celda : celdas) {
                        System.out.print(celda.text() + " | ");
                    }
                    System.out.println();
                }
            } else {
                System.out.println("No se encontró la tabla");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
