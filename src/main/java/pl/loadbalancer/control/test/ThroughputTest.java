package pl.loadbalancer.control.test;

import fish.payara.micro.BootstrapException;
import fish.payara.micro.PayaraMicro;
import fish.payara.micro.PayaraMicroRuntime;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.UUID;

@BenchmarkMode(Mode.Throughput)
public class ThroughputTest {

    @State(Scope.Thread)
    public static class Context {
        WebTarget target;

        @Setup
        public void initClient() {
            target = ClientBuilder.newClient().target("http://localhost:8080/loadbalancer/resources/route");
        }
    }

    @Benchmark
    public String executeRequest(Context context) {
        Response response = context.target.path(UUID.randomUUID().toString()).request().get();
        if (!response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            throw new IllegalStateException("Received error: " + response.getStatus());
        }
        return response.readEntity(String.class);
    }

    public static void main(String[] args) throws RunnerException, BootstrapException {
        PayaraMicroRuntime micro = PayaraMicro.getInstance()
                .addDeployment("target/loadbalancer.war")
                .bootStrap();
        Options options = new OptionsBuilder()
                .include(ThroughputTest.class.getSimpleName())
                .warmupIterations(10)
                .forks(1)
                .threads(10)
                .shouldFailOnError(true).shouldDoGC(true).build();
        new Runner(options).run();
        micro.shutdown();
    }

}
