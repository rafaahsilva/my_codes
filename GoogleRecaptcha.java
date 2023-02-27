package br.com.modapdv.captcha;

import br.com.modapdv.usecase.adapters.captcha.CaptchaValidatorAdapter;
import br.com.modapdv.usecase.exceptions.CaptchaFailureException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GoogleRecaptcha implements CaptchaValidatorAdapter {

	String chaveCaptcha;

	ObjectMapper objectMapper;

	public GoogleRecaptcha(@Value("${chave.captcha:null}")String chaveCaptcha, ObjectMapper objectMapper) {
		this.chaveCaptcha = chaveCaptcha;
		this.objectMapper = objectMapper;
	}

	@Override
	public boolean validarCaptcha(String token) {

		OkHttpClient client = new OkHttpClient();

		HttpUrl.Builder builder = HttpUrl.parse("https://www.google.com/recaptcha/api/siteverify").newBuilder();

		builder.addQueryParameter("secret", chaveCaptcha);
		builder.addQueryParameter("response", token);
		Request request = new Request.Builder()
				.url(builder.build())
				.get()
				.build();

		try {
			Response response = client.newCall(request).execute();
			if (!response.isSuccessful()) throw new RuntimeException("Erro ao fazer a requisição");

			var content = response.body().string();
			var captchReturn = objectMapper.readValue(content, GoogleRecaptchaReturn.class);
			return captchReturn.getSuccess();
		} catch (IOException e) {
			throw new CaptchaFailureException("Erro ao requisitar validação de captcha", e);
		}
	}
}
