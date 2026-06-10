import { createRoot } from "react-dom/client";
import { StrictMode, useState } from "react";
import { KcPage } from "./kc.gen";
import { kcContextMocks } from "keycloakify/login/KcContext/kcContextMocks";

if (window.kcContext !== undefined) {
    // PRODUCTION MODE (Inside Real Keycloak)
    createRoot(document.getElementById("root")!).render(
        <StrictMode>
            <KcPage kcContext={window.kcContext as any} />
        </StrictMode>
    );
} else {
    // LOCAL DEV MODE (npm run dev) - Added a cool Dropdown to switch pages!
    const DevModeApp = () => {
        const [pageId, setPageId] = useState("login.ftl");
        const mockContext = kcContextMocks.find((kc) => kc.pageId === pageId);

        return (
            <>
                {/* Floating Dropdown Menu for Dev Mode Only */}
                <div style={{ position: "fixed", top: 10, right: 10, zIndex: 99999, background: "#fff", padding: "10px", borderRadius: "8px", boxShadow: "0 4px 15px rgba(0,0,0,0.3)" }}>
                    <label style={{ marginRight: "10px", fontWeight: "bold", color: "#333" }}>🛠 Switch Page:</label>
                    <select 
                        value={pageId} 
                        onChange={(e) => setPageId(e.target.value)} 
                        style={{ padding: "5px", borderRadius: "5px", border: "1px solid #ccc", color: "#000", cursor: "pointer" }}
                    >
                        <option value="login.ftl">Login Page</option>
                        <option value="register.ftl">Register Page</option>
                        <option value="login-reset-password.ftl">Forgot Password</option>
                        <option value="login-update-password.ftl">Update Password</option>
                        <option value="error.ftl">Error Page</option>
                        <option value="info.ftl">Info Page</option>
                        <option value="login-page-expired.ftl">Session Expired</option>
                    </select>
                </div>

                {/* Render the actual Keycloak page based on selection */}
                {mockContext ? <KcPage kcContext={mockContext as any} /> : <div>Mock context not found</div>}
            </>
        );
    };

    createRoot(document.getElementById("root")!).render(<DevModeApp />);
}
