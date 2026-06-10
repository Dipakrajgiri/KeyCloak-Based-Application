import type { PageProps } from "keycloakify/login/pages/PageProps";
import type { KcContext } from "../KcContext";
import type { I18n } from "../i18n";

export default function Error(props: PageProps<Extract<KcContext, { pageId: "error.ftl" }>, I18n>) {
    const { kcContext } = props;
    const { message, client, skipLink } = kcContext;

    return (
        <>
            <div className="logo-container">
                <div className="logo-icon" style={{ background: "linear-gradient(135deg, #ff6b8a 0%, #ff4757 100%)" }}>
                    <svg viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
                    </svg>
                </div>
                <h1 className="page-title">Something Went Wrong</h1>
                <p className="page-subtitle">An error occurred during authentication</p>
            </div>

            {message !== undefined && (
                <div className="alert alert-error">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/>
                    </svg>
                    <span dangerouslySetInnerHTML={{ __html: message.summary }} />
                </div>
            )}

            {!skipLink && client !== undefined && client.baseUrl !== undefined && (
                <a href={client.baseUrl} className="btn-primary" style={{ display: "flex", alignItems: "center", justifyContent: "center", textDecoration: "none", marginTop: "16px" }}>
                    Return to Application
                </a>
            )}
        </>
    );
}
