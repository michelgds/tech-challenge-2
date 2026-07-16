package br.com.fiap.usuarios.application.usecase;

/**
 * Marca uma classe como um caso de uso da aplicação (Interactor), seguindo o padrão
 * de Clean Architecture: uma classe por ação de negócio, com um único método
 * {@code execute}, responsabilidade única e independente de detalhes de framework web.
 * <p>
 * Não força uma assinatura genérica de método porque cada caso de uso tem uma
 * entrada/saída naturalmente distinta (parâmetro único, comando com múltiplos campos,
 * ou nenhum parâmetro), mas todos devem expor um método público {@code execute(...)}.
 */
public interface UseCase {
}
