import { useState, type FormEventHandler } from "react";
import type { PageProps } from "keycloakify/login/pages/PageProps";
import type { KcContext } from "../KcContext";
import type { I18n } from "../i18n";
import UserProfileFormFields from "keycloakify/login/UserProfileFormFields";
import type { Attribute } from "keycloakify/login/KcContext";

export default function Register(props: PageProps<Extract<KcContext, { pageId: "register.ftl" }>, I18n>) {
    const { kcContext, i18n } = props;
    const { url, recaptchaRequired, recaptchaSiteKey, termsAcceptanceRequired } = kcContext;

    const [isFormSubmittable, setIsFormSubmittable] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const onSubmit: FormEventHandler<HTMLFormElement> = () => {
        setIsSubmitting(true);
        return true;
    };

    return (
        <>
            {/* Header */}
            <div className="logo-container">
                <div className="logo-icon">
                    <svg viewBox="0 0 24 24" fill="currentColor">
                        <path d="M15 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm-9-2V7H4v3H1v2h3v3h2v-3h3v-2H6zm9 4c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                    </svg>
                </div>
                <h1 className="page-title">Create Account</h1>
                <p className="page-subtitle">Join us and start managing your inventory</p>
            </div>

            {/* Register Form */}
            <form onSubmit={onSubmit} action={url.registrationAction} method="post">
                <UserProfileFormFields
                    kcContext={kcContext}
                    onIsFormSubmittableValueChange={setIsFormSubmittable}
                    i18n={i18n}
                    kcClsx={() => ""}
                    doMakeUserConfirmPassword={true}
                    BeforeField={({ attribute }: { attribute: Attribute }) => (
                        <div className="form-group" key={attribute.name}>
                            <label className="form-label" htmlFor={attribute.name}>
                                {attribute.displayName ?? attribute.name}
                                {attribute.required && <span style={{ color: "var(--error)", marginLeft: "4px" }}>*</span>}
                            </label>
                        </div>
                    )}
                />

                {termsAcceptanceRequired && (
                    <div className="form-group">
                        <div className="remember-me">
                            <input type="checkbox" id="termsAccepted" name="termsAccepted" required />
                            <label htmlFor="termsAccepted">
                                I agree to the <a href="#" className="link">Terms of Service</a> and <a href="#" className="link">Privacy Policy</a>
                            </label>
                        </div>
                    </div>
                )}

                {recaptchaRequired && recaptchaSiteKey && (
                    <div className="form-group">
                        <div className="g-recaptcha" data-sitekey={recaptchaSiteKey} data-size="normal" />
                    </div>
                )}

                <button
                    type="submit"
                    className="btn-primary"
                    disabled={!isFormSubmittable || isSubmitting}
                    style={{ marginTop: "8px" }}
                >
                    {isSubmitting ? <span className="spinner" /> : "Create Account"}
                </button>
            </form>

            {/* Login Link */}
            <div className="card-footer">
                <span>Already have an account?</span>
                <a href={url.loginUrl} className="link">Sign in</a>
            </div>
        </>
    );
}
