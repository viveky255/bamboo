package bamboo1;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;
import com.atlassian.bamboo.specs.api.builders.repository.VcsRepository;
import com.atlassian.bamboo.specs.builders.repository.git.GitRepository;
import com.atlassian.bamboo.specs.builders.task.ScriptTask;
import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;

/**
 * Plan configuration for Bamboo.
 *
 * @see <a href="https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs">Bamboo Specs</a>
 */
@BambooSpec
public class PlanSpec {

    /**
     * Run 'main' to publish your plan.
     */
    public static void main(String[] args) throws Exception {
        // by default credentials are read from the '.credentials' file
        BambooServer bambooServer = new BambooServer("http://localhost:8085");

        Plan plan = new PlanSpec().createPlan();
        bambooServer.publish(plan);

        PlanPermissions planPermission = new PlanSpec().createPlanPermission(plan.getIdentifier());
        bambooServer.publish(planPermission);
    }

    PlanPermissions createPlanPermission(PlanIdentifier planIdentifier) {
        Permissions permissions = new Permissions()
                .userPermissions("admin", PermissionType.ADMIN)
                .groupPermissions("bamboo-admin", PermissionType.ADMIN)
                .loggedInUserPermissions(PermissionType.BUILD)
                .anonymousUserPermissionView();

        return new PlanPermissions(planIdentifier)
                .permissions(permissions);
    }

    Project project() {
        return new Project()
                .name("My Project")
                .key("PROJ");
    }

    Plan createPlan() {
        return new Plan(project(), "My Plan", "PLAN")
                .description("Plan created from Bamboo Java Specs")
                .planRepositories(
                        gitRepository()
                )
                .stages(
                        new Stage("Stage 1")
                                .jobs(
                                        new Job("Job 1", "JOB1")
                                                .tasks(
                                                        gitRepositoryCheckoutTask(),
                                                        scriptTask()
                                                )
                                                .artifacts(
                                                        artifact()
                                                )
                                )
                );
    }

    VcsRepository gitRepository() {
        return new GitRepository()
                .name("bamboo-specs")
                .url("git@bitbucket.org:atlassian/bamboo-specs.git")
                .branch("master");
    }

    VcsCheckoutTask gitRepositoryCheckoutTask() {
        return new VcsCheckoutTask()
                .addCheckoutOfDefaultRepository();
    }

    ScriptTask scriptTask() {
        return new ScriptTask()
                .inlineBody("mkdir target; echo 'hello world' > target/console.out")
                .interpreterShell();
    }

    Artifact artifact() {
        return new Artifact("Build results")
                .location("target")
                .copyPattern("**/*");
    }
}
