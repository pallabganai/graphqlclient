package com.example.graphqlclient;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.graphql.client.RSocketGraphQlClient;

@SpringBootApplication
public class GraphqlclientApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraphqlclientApplication.class, args);
	}

	@Bean
	public HttpGraphQlClient httpGraphQlClient() {
		return HttpGraphQlClient
				.builder()
				.url("http://localhost:8080/graphql")
				.build();
	}

	@Bean
	public RSocketGraphQlClient rSocketGraphQlClient() {
		return RSocketGraphQlClient
				.builder()
				.tcp("localhost", 9191)
				.route("graphql")
				.build();
	}

	@Bean
	ApplicationRunner applicationRunner(
			HttpGraphQlClient httpGraphQlClient,
			RSocketGraphQlClient rSocketGraphQlClient) {
		return new ApplicationRunner() {
			@Override
			public void run(ApplicationArguments args) throws Exception {
				var httpGraphQlRequest = """
							query {
							  getStudents {
								id
								name
							  }
							}
						""";

				httpGraphQlClient.document(httpGraphQlRequest).retrieve("getStudents")
						.toEntityList(Student.class)
						.subscribe(System.out::println);

				var rSocketGraphQlRequest = """

							query {
							  greeting {
								greeting
							  }
							}

						""";

				rSocketGraphQlClient.document(rSocketGraphQlRequest).retrieve("greeting")
						.toEntity(Greeting.class)
						.subscribe(System.out::println);

				var rSocketGraphQlSubscriptionRequest = """

							subscription {
							  greetings {
								greeting
							  }
							}

						""";

				rSocketGraphQlClient.document(rSocketGraphQlSubscriptionRequest)
						.retrieveSubscription("greetings")
						.toEntity(Greeting.class)
						.subscribe(System.out::println);
			}
		};
	}

	record Student(Integer id, String name) {};
	record Greeting(String greeting) {};
}
