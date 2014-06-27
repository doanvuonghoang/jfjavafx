/*
 * Copyright (C) 2014 Hoàng Doãn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jfjavafx.plugins.genreport;

import com.jf.javafx.Application;
import com.jf.javafx.Controller;
import com.jf.javafx.services.Database;
import java.io.File;
import java.net.Authenticator;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Hoàng Doãn
 */
public class GenReportFormController extends Controller {

    @FXML
    ComboBox<String> cbConnection;

    @FXML
    TextArea txtEntGDTCode;

    @FXML
    TextField txtOutDir;

    public GenReportFormController(Application app) {
        super(app);
    }

    @Override
    protected void _init() {
        fillAvailableDataSources();
    }

    public void onGen_click(ActionEvent e) throws Exception {
        Connection c = Application._getService(Database.class).getConnection(cbConnection.getValue());
        PreparedStatement s = c.prepareStatement(getSQLText());

        ResultSet rs = s.executeQuery();

        while (rs.next()) {
            displayStatus("");
            int dlid = rs.getInt("DL_DOCUMENT_ID");
            int eid = rs.getInt("ENTERPRISE_ID");
            String gdtCode = rs.getString("ENTERPRISE_GDT_CODE");

            displayStatus("Found enterprise with gdt code: " + gdtCode);
            genBRC(dlid, gdtCode);
            genHAB(eid, gdtCode);
        }

        rs.close();
    }

    private void fillAvailableDataSources() {
        cbConnection.getItems().addAll(Application._getService(Database.class).getAvailableDataSources());
    }

    private String getSQLText() {
        return "SELECT MAX(DL_DOCUMENT_ID) DL_DOCUMENT_ID, ENTERPRISE_ID, ENTERPRISE_GDT_CODE FROM DL_DOCUMENT WHERE ENTERPRISE_GDT_CODE IN (" + txtEntGDTCode.getText() + ") GROUP BY ENTERPRISE_ID, ENTERPRISE_GDT_CODE";
    }

    private void displayStatus(String string) {
        System.out.println(string);
    }

    private void genBRC(int dlid, String gdtCode) throws Exception {
        File destDir = new File(txtOutDir.getText());
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        displayStatus("Prepare printout of enterprise " + gdtCode);
        URL url = new URL("http://10.1.242.16/reportserver?/Info_Products/vi/P_SE_004&rs:Format=PDF&DL_DOCUMENT_ID=" + String.valueOf(dlid));
        URLConnection uc = url.openConnection();
        uc.setDoOutput(true);
        uc.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encode("hoangdv:abcd@123".getBytes()));

        Files.copy(
                uc.getInputStream(),
                Paths.get(destDir.toString() + File.separator + gdtCode + ".BRC" + ".pdf"),
                StandardCopyOption.REPLACE_EXISTING);
        displayStatus("Generated BRC of enterprise " + gdtCode);
    }

    private void genHAB(int eid, String gdtCode) throws Exception {
        File destDir = new File(txtOutDir.getText());
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        URL url = new URL("http://10.1.242.16/reportserver?/Info_Products/vi/P_SE_007&rs:Format=PDF&ENTERPRISE_ID=" + String.valueOf(eid));
        URLConnection uc = url.openConnection();
        uc.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encode("hoangdv:abcd@123".getBytes()));

        Files.copy(
                uc.getInputStream(),
                Paths.get(destDir.toString() + File.separator + gdtCode + ".LS3N" + ".pdf"),
                StandardCopyOption.REPLACE_EXISTING);

        displayStatus("Generated LS3N of enterprise " + gdtCode);
    }
}
