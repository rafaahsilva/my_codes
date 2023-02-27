package br.com.modapdv.mail;

import br.com.modapdv.usecase.adapters.mail.MailAdapter;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class MailSender implements MailAdapter {

	String FROM = "noreply@teste.com";

	String SUBJECT = "E-mail de confirmação de conta";

	@Override
	public void enviar(String subdominio, String contaEmail) {

		StringBuilder contentBuilder = new StringBuilder();
		try {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(getClass()
					.getClassLoader()
					.getResourceAsStream("html/email.html")));
			String arquivo;
			while ((arquivo = bufferedReader.readLine()) != null) {
				contentBuilder.append(arquivo);
			}
			bufferedReader.close();
		} catch (IOException e) {
		}
		String content = contentBuilder.toString();
		content = content.replace("[email]", contaEmail);
		content = content.replace("[subdominio]", subdominio);

		String TEXTBODY = "Teste";

		try {
			AmazonSimpleEmailService client =
					AmazonSimpleEmailServiceClientBuilder.standard()
							.withRegion(Regions.US_EAST_1)
							.withCredentials(new DefaultAWSCredentialsProviderChain())
							.build();
			SendEmailRequest request = new SendEmailRequest()
					.withDestination(
							new Destination().withToAddresses(contaEmail))
					.withMessage(new Message()
							.withBody(new Body()
									.withHtml(new Content()
											.withCharset("UTF-8").withData(content))
									.withText(new Content()
											.withCharset("UTF-8").withData(TEXTBODY)))
							.withSubject(new Content()
									.withCharset("UTF-8").withData(SUBJECT)))
					.withSource(FROM);
			client.sendEmail(request);
		} catch (Exception ex) {
			throw ex;
		}
	}
}
