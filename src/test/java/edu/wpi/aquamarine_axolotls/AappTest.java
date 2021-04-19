package edu.wpi.aquamarine_axolotls;

import static org.testfx.api.FxAssert.verifyThat;

import edu.wpi.aquamarine_axolotls.views.Navigation;
import javafx.scene.Node;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;

import javax.xml.soap.Text;

/**
 * This is an integration test for the entire application. Rather than running a single scene
 * individually, it will run the entire application as if you were running it.
 */
@ExtendWith(ApplicationExtension.class)
public class AappTest extends FxRobot {

  /** Setup test suite. */
  @BeforeAll
  public static void setup() throws Exception {
    FxToolkit.registerPrimaryStage();
    FxToolkit.setupApplication(Aapp.class);
  }

  @AfterAll
  public static void cleanup() {}

  @Test
  public void testSceneLayout() {
    verifyThat("Welcome", Node::isVisible);
    verifyThat("BRIGHAM AND WOMEN'S HOSPITAL", Node::isVisible);
  }
  @Test
  public void testSignInButton() {
    clickOn("#signInB");
  }
  @Test
  public void testMapButton(){
    clickOn("#mapB");
  }
  @Test
  public void testServiceRequests(){
    clickOn("#serviceReqB");
  }
  @Test
  public void testSettingsButton(){
    clickOn("#settingsB");
  }


}