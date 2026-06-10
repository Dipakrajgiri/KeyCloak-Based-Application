import { useState, type FormEventHandler } from "react";
import type { PageProps } from "keycloakify/login/pages/PageProps";
import type { KcContext } from "../KcContext";
import type { I18n } from "../i18n";

export default function LoginResetPassword(props: PageProps<Extract<KcContext, { pageId: "login-reset-password.ftl" }>, I18n>) {
    const { kcContext, i18n } = props;
    const { url, realm } = kcContext;
    const { msg } = i18n;

    const [isSubmitting, setIsSubmitting] = useState(false);

    const onSubmit: FormEventHandler<HTMLFormElement> = () => {
        setIsSubmitting(true);
        return true;
    };

    return (
        <>
            <div className="logo-container">
                <div className="logo-icon">
                    <svg viewBox="0 0 24 24" fill="currentColor">
                        <path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z"/>
                    </svg>
                </div>
                <h1 className="page-title">Reset Password</h1>
                <p className="page-subtitle">Enter your email to receive a reset link</p>
            </div>

            <form onSubmit={onSubmit} action={url.loginAction} method="post">
                <div className="form-group">
                    <label className="form-label" htmlFor="username">
                        {!realm.loginWithEmailAllowed ? msg("username") : !realm.registrationEmailAsUsername ? msg("usernameOrEmail") : msg("email")}
                    </label>
                    <div className="input-icon-wrapper">
                        <input
                            id="username"
                            name="username"
                            type="text"
                            autoFocus
                            autoComplete="username"
                            className="form-input"
                            placeholder="Enter your email address"
                        />
                        <span className="input-icon">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/>
                            </svg>
                        </span>
                    </div>
                </div>

                <button type="submit" className="btn-primary" disabled={isSubmitting}>
                    {isSubmitting ? <span className="spinner" /> : "Send Reset Link"}
                </button>
            </form>

            <div className="card-footer">
                <span>Remember your password?</span>
                <a href={url.loginUrl} className="link">Back to Sign In</a>
            </div>
        </>
    );
}
