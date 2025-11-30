# Common Module

Module partagé contenant les configurations et utilitaires communs à plusieurs microservices du projet safe-zone.

## 📦 Contenu

### Configurations

#### S3Config
Configuration AWS S3 partagée entre `user-service` et `media-service`.

**Localisation:** `com.example.common.config.S3Config`

**Bean fourni:** 
- `S3Client` - Client AWS S3 configuré avec les credentials

**Activation:**
La configuration est conditionnelle et ne se charge que si la propriété `aws.region` est définie.

```yaml
aws:
    region: eu-north-1
    credentials:
        access-key: ${AWS_ACCESS_KEY_ID}
        secret-key: ${AWS_SECRET_ACCESS_KEY}
```

## ���� Utilisation

### Ajouter la dépendance

Dans le `pom.xml` de votre service:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>common</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### Auto-configuration

Le module utilise Spring Boot Auto-Configuration via `META-INF/spring.factories`.
Toutes les configurations sont automatiquement chargées dans les services qui dépendent de ce module.

### Utiliser S3Client

```java
@Service
public class MyService {
    
    private final S3Client s3Client;
    
    @Autowired
    public MyService(S3Client s3Client) {
        this.s3Client = s3Client;
    }
    
    public void uploadFile(String bucket, String key, byte[] data) {
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build(),
            RequestBody.fromBytes(data)
        );
    }
}
```

## 🔧 Configuration Conditionnelle

### S3Config
- **Condition:** `aws.region` doit être défini
- **Profils:** Compatible avec tous les profils (dev, test, prod)
- **Tests:** Automatiquement désactivé si `aws.region` n'est pas défini

### Environnement de Test

Dans `application.yml` de test:
```yaml
# S3Config sera automatiquement désactivé
# Pas besoin de définir aws.region
```

## 📋 Dépendances

### Runtime
- Spring Boot Starter
- AWS SDK for Java - S3 (2.32.13)

### Scope
- Tous les beans sont créés avec scope `singleton` par défaut

## 🎯 Avantages

1. **DRY** - Pas de duplication de code
2. **Maintenabilité** - Une seule configuration à maintenir
3. **Cohérence** - Configuration identique pour tous les services
4. **Flexibilité** - Activation conditionnelle basée sur les propriétés
5. **Testabilité** - Facile à mocker ou désactiver dans les tests

## 📝 Futures Additions

Ce module peut être étendu pour inclure:

### Configurations Candidates
- **KafkaConfig** - Configuration Kafka partagée
- **MongoConfig** - Configuration MongoDB de base
- **CorsConfig** - Configuration CORS commune
- **SwaggerConfig** - Configuration OpenAPI/Swagger

### Utilitaires
- **DateUtils** - Manipulation de dates
- **StringUtils** - Utilitaires de chaînes
- **ValidationUtils** - Validations personnalisées
- **SecurityUtils** - Utilitaires de sécurité

### DTOs Partagés
- **ErrorResponse** - Réponses d'erreur standardisées
- **PageResponse** - Réponses paginées
- **ApiResponse** - Wrapper de réponse générique

### Constantes
- **ApplicationConstants** - Constantes globales
- **MessageConstants** - Messages d'erreur/succès
- **RegexPatterns** - Patterns regex réutilisables

## 🏗️ Structure

```
common/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── example/
        │           └── common/
        │               ├── config/          # Configurations Spring
        │               │   └── S3Config.java
        │               ├── constants/       # (futur) Constantes
        │               ├── dto/             # (futur) DTOs partagés
        │               └── util/            # (futur) Utilitaires
        └── resources/
            └── META-INF/
                └── spring.factories         # Auto-configuration
```

## 🧪 Tests

Le module common n'a pas de tests car il contient uniquement des configurations.
Les configurations sont testées indirectement via les tests des services qui les utilisent.

## 📚 Documentation

- [DEDUPLICATION-S3CONFIG.md](../DEDUPLICATION-S3CONFIG.md) - Détails de la déduplication
- [RESUME-COMPLET.md](../RESUME-COMPLET.md) - Résumé complet du projet

## 🔗 Services Utilisant ce Module

- ✅ **user-service** - Utilise S3Config
- ✅ **media-service** - Utilise S3Config
- 🔮 **Futurs services** - Peuvent utiliser ce module

## 📞 Support

Pour toute question ou suggestion d'amélioration du module common, contactez l'équipe de développement.

