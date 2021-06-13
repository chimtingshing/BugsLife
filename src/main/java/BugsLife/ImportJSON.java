package BugsLife;

import com.google.gson.Gson;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ImportJSON extends javax.swing.JDialog {

    private String filename;
    private ArrayList<Project> projects = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private JSONObject jsonObj;
    private JSONArray jsonArrProject;
    private JSONArray jsonArrUser;
    private CommentsDAO commentDAO;
    private ProjectsDAO projectDAO;
    private UsersDAO userDAO;
    private IssuesDAO issueDAO;
    private ReactsDAO reactDAO;

    /**
     * Creates new form ImportJSON
     */
    public ImportJSON(java.awt.Frame parent, boolean modal, String filename) throws SQLException {
        super(parent, modal);

        this.commentDAO = new CommentsDAO();
        this.projectDAO = new ProjectsDAO();
        this.userDAO = new UsersDAO();
        this.issueDAO = new IssuesDAO();
        this.reactDAO = new ReactsDAO();
        this.filename = filename;
        
        initComponents();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        
        //start the SwingWorker thread to execute importing codes
        startThread();
    }

    public void startThread() {
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                bar.setIndeterminate(true);
                //clear all database
                userDAO.clear();
                reactDAO.clear();
                commentDAO.clear();
                issueDAO.clear();
                projectDAO.clear();

                Gson gson = new Gson();
                JSONParser parser = new JSONParser();
                try {
                    FileReader reader = new FileReader(filename);
                    jsonObj = (JSONObject) parser.parse(reader);
                    jsonArrProject = (JSONArray) jsonObj.get("projects");
                    jsonArrUser = (JSONArray) jsonObj.get("users");

                    //get array list of projects from json file
                    for (int i = 0; i < jsonArrProject.size(); i++) {
                        projects.add(gson.fromJson(jsonArrProject.get(i).toString(), Project.class));
                    }

                    //get array list of users from project file
                    for (int i = 0; i < jsonArrUser.size(); i++) {
                        users.add(gson.fromJson(jsonArrUser.get(i).toString(), User.class));
                    }

                    //add projects into the database
                    for (int i = 0; i < projects.size(); i++) {
                        projectDAO.addProject(projects.get(i));
                    }

                    //add the issues into the database
                    for (int i = 0; i < projects.size(); i++) {
                        for (int j = 0; j < projects.get(i).getIssues().size(); j++) {
                            issueDAO.importIssue(projects.get(i).getIssues().get(j), (int) projects.get(i).getId());
                        }
                    }

                    //add each comment into the database
                    for (int i = 0; i < projects.size(); i++) {
                        for (int j = 0; j < projects.get(i).getIssues().size(); j++) {
                            for (int k = 0; k < projects.get(i).getIssues().get(j).getCnoimg().size(); k++) {
                                CommentWithoutImage tempComment = projects.get(i).getIssues().get(j).getCnoimg().get(k);
                                Comment comment = new Comment(tempComment.getComment_id(), tempComment.getText(), tempComment.getTimestamp(), tempComment.getUser());
                                commentDAO.importComment(comment, projects.get(i).getIssues().get(j).getId());
                            }
                        }
                    }

                    //add each user nito the database
                    for (int i = 0; i < users.size(); i++) {
                        userDAO.addUser(users.get(i));
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
                bar.setIndeterminate(false);
                return null;
            }

            protected void done() {
                JOptionPane.showMessageDialog(null, "Done!");
                dispose();
                new Login();
            }
        };
        sw.execute();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bar = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Importing the JSON file and overwriting data, please wait");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bar, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(59, 59, 59))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bar, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(ImportJSON.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(ImportJSON.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(ImportJSON.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(ImportJSON.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the dialog */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                ImportJSON dialog = new ImportJSON(new javax.swing.JFrame(), true);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                    @Override
//                    public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar bar;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
