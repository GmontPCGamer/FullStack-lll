# Plan de Branching - Municipalidad Valle del Sol

Para garantizar un control de versiones colaborativo y eficiente, hemos implementado la estrategia de branching **Git Flow** (simplificada para este contexto).

## 1. Ramas Principales
- **`main` / `master`**: Es la rama de producción. Contiene el código estable y funcional. Nadie hace commits directos en esta rama. Los merges a `main` ocurren solo a través de Pull Requests aprobados.
- **`develop`**: Es la rama de integración. Todo el código de nuevas características (features) se une en `develop` para pruebas.

## 2. Ramas de Soporte
- **`feature/<nombre-feature>`**: Utilizada para desarrollar nuevas características o componentes. (Ejemplo: `feature/reportes-ms`, `feature/bff-web`). Se crean a partir de `develop` y se fusionan de vuelta a `develop`.
- **`hotfix/<nombre-error>`**: Creada directamente desde `main` para resolver bugs urgentes en producción.
- **`bugfix/<nombre-bug>`**: Creada desde `develop` para resolver errores encontrados durante la fase de testing.

## 3. Flujo de Trabajo (Workflow)
1. Un desarrollador clona el repositorio y se posiciona en `develop` (`git checkout develop`).
2. Crea una rama para su tarea (`git checkout -b feature/crear-alerta`).
3. Realiza commits frecuentes y descriptivos (`git commit -m "feat: implementar endpoint POST en alertas-ms"`).
4. Sube la rama remota (`git push origin feature/crear-alerta`).
5. Abre un **Pull Request (PR)** hacia `develop`.
6. El equipo revisa el código. Si hay conflictos de fusión (Merge Conflicts), el desarrollador actualiza su rama (`git pull origin develop`) y resuelve los conflictos localmente antes de completar el PR.
7. Una vez aprobado, se fusiona en `develop`.

## 4. Gestión de Conflictos
Si dos desarrolladores modifican el mismo archivo (ej. `pom.xml` en el `businessdomain` padre), Git informará de un conflicto al intentar hacer pull o merge.
**Resolución:**
- El desarrollador ejecutará `git merge develop` en su rama de feature.
- Git marcará el archivo conflictivo con `<<<<<<< HEAD` y `>>>>>>> develop`.
- El desarrollador abrirá el archivo en su IDE, decidirá qué código mantener (aceptar ambos, actual, entrante) y guardará.
- Finalmente, se agrega y hace commit (`git add .` -> `git commit -m "fix: resolve merge conflicts in pom.xml"`).

Esta estructura favoreció la colaboración al permitir que distintos miembros trabajaran simultáneamente en Frontend, `reportes-ms` y `alertas-ms` sin sobrescribirse sus avances.
