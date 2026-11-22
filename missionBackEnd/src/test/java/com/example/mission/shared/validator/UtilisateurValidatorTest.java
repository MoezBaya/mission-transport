package com.example.mission.shared.validator;

import com.example.mission.user.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UtilisateurValidatorTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private UtilisateurValidator utilisateurValidator;

    // ============================================================
    // TESTS validerChampsObligatoires
    // ============================================================

    @Test
    void validerChampsObligatoires_devraitPasserSiToutEstOk() {
        assertThatCode(() -> utilisateurValidator.validerChampsObligatoires(
                "Ben Ali", "Ahmed", "12345678", "ahmed@mail.com", "password123"
        )).doesNotThrowAnyException();
    }

    @Test
    void validerChampsObligatoires_devraitLancerExceptionSiNomVide() {
        assertThatThrownBy(() -> utilisateurValidator.validerChampsObligatoires(
                "", "Ahmed", "12345678", "ahmed@mail.com", "password123"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nom");
    }

    @Test
    void validerChampsObligatoires_devraitLancerExceptionSiNomNull() {
        assertThatThrownBy(() -> utilisateurValidator.validerChampsObligatoires(
                null, "Ahmed", "12345678", "ahmed@mail.com", "password123"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nom");
    }

    @Test
    void validerChampsObligatoires_devraitLancerExceptionSiPrenomVide() {
        assertThatThrownBy(() -> utilisateurValidator.validerChampsObligatoires(
                "Ben Ali", "", "12345678", "ahmed@mail.com", "password123"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("prénom");
    }

    @Test
    void validerChampsObligatoires_devraitLancerExceptionSiCinVide() {
        assertThatThrownBy(() -> utilisateurValidator.validerChampsObligatoires(
                "Ben Ali", "Ahmed", "", "ahmed@mail.com", "password123"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CIN");
    }

    @Test
    void validerChampsObligatoires_devraitLancerExceptionSiEmailVide() {
        assertThatThrownBy(() -> utilisateurValidator.validerChampsObligatoires(
                "Ben Ali", "Ahmed", "12345678", "", "password123"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
    }

    @Test
    void validerChampsObligatoires_devraitLancerExceptionSiMdpTropCourt() {
        assertThatThrownBy(() -> utilisateurValidator.validerChampsObligatoires(
                "Ben Ali", "Ahmed", "12345678", "ahmed@mail.com", "123"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("6 caractères");
    }

    // ============================================================
    // TESTS validerChampsModification
    // ============================================================

    @Test
    void validerChampsModification_devraitPasserSiToutEstOk() {
        assertThatCode(() -> utilisateurValidator.validerChampsModification(
                "Ben Ali", "Ahmed"
        )).doesNotThrowAnyException();
    }

    @Test
    void validerChampsModification_devraitLancerExceptionSiNomVide() {
        assertThatThrownBy(() -> utilisateurValidator.validerChampsModification("", "Ahmed"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nom");
    }

    @Test
    void validerChampsModification_devraitLancerExceptionSiPrenomVide() {
        assertThatThrownBy(() -> utilisateurValidator.validerChampsModification("Ben Ali", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("prénom");
    }

    // ============================================================
    // TESTS verifierUnicite
    // ============================================================

    @Test
    void verifierUnicite_devraitPasserSiUnique() {
        when(utilisateurRepository.existsByCin("12345678")).thenReturn(false);
        when(utilisateurRepository.existsByEmail("ahmed@mail.com")).thenReturn(false);

        assertThatCode(() -> utilisateurValidator.verifierUnicite(
                "12345678", "ahmed@mail.com"
        )).doesNotThrowAnyException();
    }

    @Test
    void verifierUnicite_devraitLancerExceptionSiCinExiste() {
        when(utilisateurRepository.existsByCin("12345678")).thenReturn(true);

        assertThatThrownBy(() -> utilisateurValidator.verifierUnicite(
                "12345678", "ahmed@mail.com"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CIN");
    }

    @Test
    void verifierUnicite_devraitLancerExceptionSiEmailExiste() {
        when(utilisateurRepository.existsByCin("12345678")).thenReturn(false);
        when(utilisateurRepository.existsByEmail("ahmed@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> utilisateurValidator.verifierUnicite(
                "12345678", "ahmed@mail.com"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
    }

    // ============================================================
    // TESTS verifierUniciteModification
    // ============================================================

    @Test
    void verifierUniciteModification_devraitPasserSiPasChange() {
        // CIN et email identiques → pas de vérification
        assertThatCode(() -> utilisateurValidator.verifierUniciteModification(
                "12345678", "ahmed@mail.com",
                "12345678", "ahmed@mail.com"
        )).doesNotThrowAnyException();

        verify(utilisateurRepository, never()).existsByCin(any());
        verify(utilisateurRepository, never()).existsByEmail(any());
    }

    @Test
    void verifierUniciteModification_devraitPasserSiNouveauCinUnique() {
        when(utilisateurRepository.existsByCin("99999999")).thenReturn(false);

        assertThatCode(() -> utilisateurValidator.verifierUniciteModification(
                "12345678", "ahmed@mail.com",
                "99999999", "ahmed@mail.com"
        )).doesNotThrowAnyException();
    }

    @Test
    void verifierUniciteModification_devraitLancerExceptionSiNouveauCinExiste() {
        when(utilisateurRepository.existsByCin("99999999")).thenReturn(true);

        assertThatThrownBy(() -> utilisateurValidator.verifierUniciteModification(
                "12345678", "ahmed@mail.com",
                "99999999", "ahmed@mail.com"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CIN");
    }

    @Test
    void verifierUniciteModification_devraitLancerExceptionSiNouvelEmailExiste() {
        when(utilisateurRepository.existsByEmail("nouveau@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> utilisateurValidator.verifierUniciteModification(
                "12345678", "ahmed@mail.com",
                "12345678", "nouveau@mail.com"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
    }
}
