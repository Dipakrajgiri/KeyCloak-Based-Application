import type { PageProps } from "keycloakify/login/pages/PageProps";
import type { KcContext } from "../KcContext";
import type { I18n } from "../i18n";

export default function Info(props: PageProps<Extract<KcContext, { pageId: "info.ftl" }>, I18n>) {
    const { kcContext, i18n } = props;
    const { messageHeader, message, requiredActions, skipLink, pageRedirectUri, actionUri, client } = kcContext;
    const { msg, advancedMsg } = i18n;

    return (
        <>
            <div className="logo-container">
                <div className="logo-icon" style={{ background: "linear-gradient(135deg, #00bcd4 0%, #00d4aa 100%)" }}>
                    <svg viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                    </svg>
                </div>
                <h1 className="page-title">{messageHeader !== undefined ? advancedMsg(messageHeader) : msg("doSubmit")}</h1>
            </div>

            {message !== undefined && (
                <div className="alert alert-info" style={{ marginBottom: "24px" }}>
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/>
                    </svg>
                    <span dangerouslySetInnerHTML={{ __html: message.summary }} />
                </div>
            )}

            {requiredActions && (
                <div style={{ marginBottom: "24px" }}>
                    {requiredActions.map(action => (
                        <div key={action} className="alert alert-warning">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                            <span>{advancedMsg(`requiredAction.${action}` as const)}</span>
                        </div>
                    ))}
                </div>
            )}

            {!skipLink && (
                <div style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
                    {pageRedirectUri && (
                        <a href={pageRedirectUri} className="btn-primary" style={{ display: "flex", alignItems: "center", justifyContent: "center", textDecoration: "none" }}>
                            {msg("backToApplication")}
                        </a>
                    )}
                    {actionUri && (
                        <a href={actionUri} className="btn-primary" style={{ display: "flex", alignItems: "center", justifyContent: "center", textDecoration: "none" }}>
                            {msg("proceedWithAction")}
                        </a>
                    )}
                    {client && client.baseUrl && !pageRedirectUri && !actionUri && (
                        <a href={client.baseUrl} className="btn-secondary" style={{ textDecoration: "none" }}>
                            Return to Application
                        </a>
                    )}
                </div>
            )}
        </>
    );
}
